package ru.my.cryptotracker.model.repository

import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.model.entities.CoinUiModel
import ru.my.cryptotracker.model.entities.MarketCoin

interface CryptoMarketRepository {

    fun listenCachedCoins(): Flow<List<CoinUiModel>>

    fun listenCachedMarketCoins(): Flow<List<MarketCoin>>
}