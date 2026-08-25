package ru.my.cryptotracker.model.repository

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.my.cryptotracker.core.model.MarketCoin
import ru.my.cryptotracker.model.database.CryptoDatabase
import ru.my.cryptotracker.model.mappers.toMarketCoins
import ru.my.cryptotracker.model.mappers.toUiModelsList
import ru.my.cryptotracker.model.entities.CoinUiModel

class CryptoMarketRepositoryImpl @Inject constructor(
    private val db: CryptoDatabase
) : CryptoMarketRepository {

    override fun listenCachedCoins(): Flow<List<CoinUiModel>> {
        return db.coinDao.listenAllCachedCoins()
            .map { it.toUiModelsList() }
    }

    override fun listenCachedMarketCoins(): Flow<List<MarketCoin>> {
        return db.coinDao.listenAllCachedCoins()
            .map { it.toMarketCoins() }
    }
}