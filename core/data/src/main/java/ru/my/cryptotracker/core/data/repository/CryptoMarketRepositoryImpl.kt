package ru.my.cryptotracker.core.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.my.cryptotracker.core.domain.repository.CryptoMarketRepository
import ru.my.cryptotracker.core.model.MarketCoin
import ru.my.cryptotracker.core.data.database.CryptoDatabase
import ru.my.cryptotracker.core.data.mappers.toMarketCoinList

class CryptoMarketRepositoryImpl @Inject constructor(
    private val db: CryptoDatabase
) : CryptoMarketRepository {

    override fun listenCachedMarketCoins(): Flow<List<MarketCoin>> {
        return db.coinDao.listenAllCachedCoins()
            .map { it.toMarketCoinList() }
    }
}