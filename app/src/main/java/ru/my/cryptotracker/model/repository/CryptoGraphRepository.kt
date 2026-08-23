package ru.my.cryptotracker.model.repository

import ru.my.cryptotracker.model.entities.GraphPoint

interface CryptoGraphRepository {
    suspend fun loadCoinHistoryUseCase(coinId: String): List<GraphPoint>
}