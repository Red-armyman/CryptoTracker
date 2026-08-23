package ru.my.cryptotracker.usecase.portfolio

import ru.my.cryptotracker.model.repository.CryptoPortfolioRepository
import ru.my.cryptotracker.model.security.EncryptedPreferencesManager
import javax.inject.Inject

class ClearPortfolioUseCase @Inject constructor(
    private val portfolioRepository: CryptoPortfolioRepository,
    private val securityManager: EncryptedPreferencesManager
) {
    suspend operator fun invoke() {
        portfolioRepository.clearPortfolio()
        securityManager.clearSecureStorage()
    }
}