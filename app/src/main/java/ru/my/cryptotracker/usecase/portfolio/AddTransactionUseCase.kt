package ru.my.cryptotracker.usecase.portfolio

import ru.my.cryptotracker.model.repository.CryptoPortfolioRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val portfolioRepository: CryptoPortfolioRepository
) {
    suspend operator fun invoke(coinId: String, amount: Double, purchasePrice: Double, timestamp: Long) {
        portfolioRepository.addTransaction(coinId, amount, purchasePrice, timestamp)
    }
}