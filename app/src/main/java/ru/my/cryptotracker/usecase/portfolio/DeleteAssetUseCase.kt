package ru.my.cryptotracker.usecase.portfolio

import ru.my.cryptotracker.model.repository.CryptoPortfolioRepository
import javax.inject.Inject

class DeleteAssetUseCase @Inject constructor(
    private val portfolioRepository: CryptoPortfolioRepository
) {
    suspend operator fun invoke(coinId: String) {
        portfolioRepository.deleteAssetByCoinId(coinId)
    }
}