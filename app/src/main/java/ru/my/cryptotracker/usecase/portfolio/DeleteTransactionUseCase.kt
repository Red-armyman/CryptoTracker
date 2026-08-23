package ru.my.cryptotracker.usecase.portfolio

import jakarta.inject.Inject
import ru.my.cryptotracker.model.repository.CryptoPortfolioRepository

class DeleteTransactionUseCase @Inject constructor(
    private val portfolioRepository: CryptoPortfolioRepository
) {
    suspend operator fun invoke(transactionId: Long) {
        portfolioRepository.deleteTransaction(transactionId)
    }
}