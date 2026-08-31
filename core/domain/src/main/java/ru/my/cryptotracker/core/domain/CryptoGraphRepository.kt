package ru.my.cryptotracker.core.domain

import ru.my.cryptotracker.core.model.GraphPoint

interface CryptoGraphRepository {
    suspend fun loadCoinHistoryUseCase(coinId: String): List<GraphPoint>
}