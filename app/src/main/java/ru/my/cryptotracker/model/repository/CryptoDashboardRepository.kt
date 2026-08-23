package ru.my.cryptotracker.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.my.cryptotracker.model.entities.CoinUiModel
import ru.my.cryptotracker.model.network.NetworkResult

interface CryptoDashboardRepository {
    val isDataStaleFlow: StateFlow<Boolean>

    fun listenLocalCoinsCache(): Flow<NetworkResult<List<CoinUiModel>>>
    suspend fun refreshCoinsNetworkCache()
}