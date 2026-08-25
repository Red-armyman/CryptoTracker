package ru.my.cryptotracker.model.repository

import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.core.model.MarketCoin
import ru.my.cryptotracker.model.entities.CoinUiModel

interface CryptoMarketRepository {

    fun listenCachedCoins(): Flow<List<CoinUiModel>>

    fun listenCachedMarketCoins(): Flow<List<MarketCoin>>
}