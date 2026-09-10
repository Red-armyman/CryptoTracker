package ru.my.cryptotracker.core.domain.usecase.cryptoGraph

import ru.my.cryptotracker.core.domain.repository.CryptoGraphRepository
import ru.my.cryptotracker.core.model.GraphPoint
import javax.inject.Inject

class LoadCoinHistoryUseCase @Inject constructor(
    private val repository: CryptoGraphRepository
) {
    suspend operator fun invoke(coinId: String): List<GraphPoint> =
        repository.loadCoinHistoryUseCase(coinId)
}