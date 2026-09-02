package ru.my.cryptotracker.usecase.portfolio

import kotlinx.coroutines.flow.first
import ru.my.cryptotracker.core.domain.repository.CryptoPortfolioRepository
import ru.my.cryptotracker.core.data.security.EncryptedPreferencesManager
import javax.inject.Inject

class PortfolioUseCase @Inject constructor(
    private val portfolioRepository: CryptoPortfolioRepository,
    private val encryptedPreferencesManager: EncryptedPreferencesManager
) {

    suspend fun addTransaction(
        coinId: String,
        amount: Double,
        purchasePrice: Double,
        timestamp: Long
    ) {
        portfolioRepository.addTransaction(coinId, amount, purchasePrice, timestamp)
    }

    suspend fun deleteTransaction(transactionId: Long) {
        portfolioRepository.deleteTransaction(transactionId)
    }

    suspend fun deleteAsset(coinId: String) {
        portfolioRepository.deleteAssetByCoinId(coinId)
    }

    suspend fun clearPortfolio() {
        portfolioRepository.clearPortfolio()
        encryptedPreferencesManager.clearSecureStorage()
    }

    suspend fun toggleBalanceVisibility() {
        val currentHiddenState =
            encryptedPreferencesManager.securePreferences.first().isBalanceHidden
        encryptedPreferencesManager.setBalanceVisibility(!currentHiddenState)
    }
}