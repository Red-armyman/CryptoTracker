package ru.my.cryptotracker.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.my.cryptotracker.core.model.MarketCoin

interface CryptoDashboardRepository {
    val isDataStaleFlow: StateFlow<Boolean>

    fun listenLocalCoinsCache(): Flow<List<MarketCoin>>
    suspend fun refreshCoinsNetworkCache()
}