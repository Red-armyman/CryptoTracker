package ru.my.cryptotracker.usecase.portfolio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.my.cryptotracker.core.model.PortfolioAsset
import ru.my.cryptotracker.core.model.PortfolioOverview
import ru.my.cryptotracker.core.domain.CryptoMarketRepository
import ru.my.cryptotracker.core.domain.CryptoPortfolioRepository
import ru.my.cryptotracker.model.security.EncryptedPreferencesManager
import javax.inject.Inject

/**
 * Пересчитывает балансы активов на основе Room-транзакций и котировок SQLite кэша Дашборда.
 * Автоматически кэширует итоговую сумму в зашифрованный Proto DataStore по алгоритму AES-256!
 */
class GetPortfolioOverviewUseCase @Inject constructor(
    private val portfolioRepository: CryptoPortfolioRepository,
    private val marketRepository: CryptoMarketRepository,
    private val securityManager: EncryptedPreferencesManager
) {

    operator fun invoke(): Flow<PortfolioOverview> {
        return combine(
            portfolioRepository.listenDomainAssets(),
            marketRepository.listenCachedMarketCoins(),
        ) { domainAssets, marketCoins ->

            if (domainAssets.isEmpty()) {
                securityManager.saveSecureBalance(0.0)

                return@combine PortfolioOverview(
                    assets = emptyList(),
                    marketCoins = marketCoins,
                    totalBalanceUsd = 0.0,
                    totalProfitLossUsd = 0.0,
                    totalProfitLossPercent = 0.0,
                    isOffline = false,
                )
            }

            var totalPortfolioValueUsd = 0.0
            var totalPortfolioInvestmentUsd = 0.0

            val portfolioAssets = domainAssets.mapNotNull { domainAsset ->
                val marketCoin = marketCoins.find { it.id == domainAsset.coinId }
                    ?: return@mapNotNull null

                val livePriceDouble = marketCoin.priceUsd

                val currentMarketValue = domainAsset.totalAmount * livePriceDouble
                val assetPnLUsd = currentMarketValue - domainAsset.totalInvestmentUsd

                totalPortfolioValueUsd += currentMarketValue
                totalPortfolioInvestmentUsd += domainAsset.totalInvestmentUsd

                PortfolioAsset(
                    coinId = domainAsset.coinId,
                    displayTicker = marketCoin.symbol.uppercase(),
                    imageUrl = marketCoin.imageUrl,
                    totalAmount = domainAsset.totalAmount,
                    currentPriceUsd = livePriceDouble,
                    marketValueUsd = currentMarketValue,
                    averagePurchasePrice = domainAsset.averagePurchasePrice,
                    profitLossUsd = assetPnLUsd,
                    profitLossPercent = if (domainAsset.totalInvestmentUsd > 0.0) {
                        (assetPnLUsd / domainAsset.totalInvestmentUsd) * 100.0
                    } else {
                        0.0
                    }
                )
            }

            val totalPnLUsd = totalPortfolioValueUsd - totalPortfolioInvestmentUsd
            val totalPnLPercent =
                if (totalPortfolioInvestmentUsd > 0.0) (totalPnLUsd / totalPortfolioInvestmentUsd) * 100.0 else 0.0

            securityManager.saveSecureBalance(totalPortfolioValueUsd)

            PortfolioOverview(
                assets = portfolioAssets,
                marketCoins = marketCoins,
                totalBalanceUsd = totalPortfolioValueUsd,
                totalProfitLossUsd = totalPnLUsd,
                totalProfitLossPercent = totalPnLPercent,
                isOffline = false,
            )
        }
    }
}