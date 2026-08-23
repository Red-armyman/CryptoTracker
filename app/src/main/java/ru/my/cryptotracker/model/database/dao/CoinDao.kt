package ru.my.cryptotracker.model.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.model.database.entity.CoinDb

@Dao
interface CoinDao {
    @Query("SELECT * FROM cached_coins")
    fun listenAllCachedCoins(): Flow<List<CoinDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinsCache(coins: List<CoinDb>)

    @Query("DELETE FROM cached_coins")
    suspend fun clearCoinsCache()

    @Query("SELECT * FROM cached_coins")
    suspend fun getCurrentCachedCoinsOnce(): List<CoinDb>

    @Transaction
    suspend fun updateCoinsCache(coins: List<CoinDb>) {
        clearCoinsCache()
        insertCoinsCache(coins)
    }
}