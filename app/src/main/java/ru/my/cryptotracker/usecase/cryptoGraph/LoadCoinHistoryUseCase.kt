package ru.my.cryptotracker.usecase.cryptoGraph

import ru.my.cryptotracker.model.repository.CryptoGraphRepository
import ru.my.cryptotracker.model.entities.GraphPoint
import javax.inject.Inject

class LoadCoinHistoryUseCase @Inject constructor(
    private val repository: CryptoGraphRepository
) {
    suspend operator fun invoke(coinId: String): List<GraphPoint> =
        repository.loadCoinHistoryUseCase(coinId)
}