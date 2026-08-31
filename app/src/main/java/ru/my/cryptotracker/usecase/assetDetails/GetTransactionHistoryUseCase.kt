package ru.my.cryptotracker.usecase.assetDetails

import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.core.model.PortfolioTransaction
import ru.my.cryptotracker.core.domain.CryptoPortfolioRepository
import javax.inject.Inject

/**
 * Возвращает поток истории сделок по конкретной криптовалюте.
 */
class GetTransactionHistoryUseCase @Inject constructor(
    private val portfolioRepository: CryptoPortfolioRepository
) {
    operator fun invoke(coinId: String): Flow<List<PortfolioTransaction>> {
        return portfolioRepository.listenTransactionsByCoinId(coinId)
    }
}