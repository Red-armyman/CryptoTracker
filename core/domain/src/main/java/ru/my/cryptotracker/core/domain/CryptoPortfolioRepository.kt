package ru.my.cryptotracker.core.domain

import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.core.model.DomainAsset
import ru.my.cryptotracker.core.model.PortfolioTransaction

interface CryptoPortfolioRepository {

    /**
     * Слушает базу транзакций, группирует их и возвращает поток
     * агрегированных доменных активов пользователя.
     */
    fun listenDomainAssets(): Flow<List<DomainAsset>>

    /**
     * Записать факт новой сделки (купли/продажи)
     */
    suspend fun addTransaction(
        coinId: String,
        amount: Double,
        purchasePrice: Double,
        timestamp: Long
    )

    /**
     * Удалить ошибочную транзакцию из истории
     */
    suspend fun deleteTransaction(transactionId: Long)

    /**
     * Тотальный сброс кошелька
     */
    suspend fun clearPortfolio()

    suspend fun deleteAssetByCoinId(coinId: String)

    fun listenTransactionsByCoinId(coinId: String): Flow<List<PortfolioTransaction>>
}
