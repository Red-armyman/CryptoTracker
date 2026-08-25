package ru.my.cryptotracker.model.repository

import ru.my.cryptotracker.core.model.GraphPoint

interface CryptoGraphRepository {
    suspend fun loadCoinHistoryUseCase(coinId: String): List<GraphPoint>
}