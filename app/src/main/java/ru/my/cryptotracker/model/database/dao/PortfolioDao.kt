package ru.my.cryptotracker.model.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.model.database.entity.PortfolioDb

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM portfolio_transactions ORDER BY timestamp DESC")
    fun listenAllTransactions(): Flow<List<PortfolioDb>>

    /**
     * Выборочное чтение транзакций по конкретной монете
     */
    @Query("SELECT * FROM portfolio_transactions WHERE coinId = :coinId ORDER BY timestamp DESC")
    fun listenTransactionsByCoin(coinId: String): Flow<List<PortfolioDb>>

    /**
     * Запись новой сделки (купли/продажи) в локальный портфель.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PortfolioDb)

    /**
     * Удаление конкретной ошибочной сделки из истории пользователя.
     */
    @Query("DELETE FROM portfolio_transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)

    /**
     * Тотальное выжигание истории портфеля
     */
    @Query("DELETE FROM portfolio_transactions")
    suspend fun clearEntirePortfolio()

    @Query("DELETE FROM portfolio_transactions WHERE coinId = :coinId")
    suspend fun deleteTransactionsByCoinId(coinId: String)
}