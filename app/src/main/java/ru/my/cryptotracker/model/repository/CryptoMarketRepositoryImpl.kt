package ru.my.cryptotracker.model.repository

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.my.cryptotracker.core.domain.CryptoMarketRepository
import ru.my.cryptotracker.core.model.MarketCoin
import ru.my.cryptotracker.model.database.CryptoDatabase
import ru.my.cryptotracker.model.mappers.toMarketCoins

class CryptoMarketRepositoryImpl @Inject constructor(
    private val db: CryptoDatabase
) : CryptoMarketRepository {

    override fun listenCachedMarketCoins(): Flow<List<MarketCoin>> {
        return db.coinDao.listenAllCachedCoins()
            .map { it.toMarketCoins() }
    }
}