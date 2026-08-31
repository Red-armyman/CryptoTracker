package ru.my.cryptotracker.usecase.cryptoGraph

import ru.my.cryptotracker.core.model.GraphPoint
import ru.my.cryptotracker.core.domain.CryptoGraphRepository
import javax.inject.Inject

class LoadCoinHistoryUseCase @Inject constructor(
    private val repository: CryptoGraphRepository
) {
    suspend operator fun invoke(coinId: String): List<GraphPoint> =
        repository.loadCoinHistoryUseCase(coinId)
}