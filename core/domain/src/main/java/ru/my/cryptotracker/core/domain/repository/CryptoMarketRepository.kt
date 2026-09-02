package ru.my.cryptotracker.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.core.model.MarketCoin

interface CryptoMarketRepository {

    fun listenCachedMarketCoins(): Flow<List<MarketCoin>>
}