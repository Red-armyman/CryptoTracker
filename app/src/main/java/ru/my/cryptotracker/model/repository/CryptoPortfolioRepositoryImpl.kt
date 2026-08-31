package ru.my.cryptotracker.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.my.cryptotracker.core.domain.CryptoPortfolioRepository
import ru.my.cryptotracker.core.model.DomainAsset
import ru.my.cryptotracker.core.model.PortfolioTransaction
import ru.my.cryptotracker.model.database.CryptoDatabase
import ru.my.cryptotracker.model.database.entity.PortfolioDb
import ru.my.cryptotracker.model.mappers.toDomainAsset
import ru.my.cryptotracker.model.mappers.toPortfolioTransaction
import javax.inject.Inject

class CryptoPortfolioRepositoryImpl @Inject constructor(
    private val db: CryptoDatabase
) : CryptoPortfolioRepository {

    override fun listenDomainAssets(): Flow<List<DomainAsset>> {
        return db.portfolioDao.listenAllTransactions().map { flatTransactionsList ->
            if (flatTransactionsList.isEmpty()) return@map emptyList()

            flatTransactionsList
                .groupBy { it.coinId }
                .map { (coinId, transactions) ->
                    transactions.toDomainAsset(coinId)
                }
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun addTransaction(
        coinId: String,
        amount: Double,
        purchasePrice: Double,
        timestamp: Long
    ) {
        val entity = PortfolioDb(
            coinId = coinId,
            amount = amount,
            purchasePrice = purchasePrice,
            timestamp = timestamp
        )
        db.portfolioDao.insertTransaction(entity)
    }

    override suspend fun deleteTransaction(transactionId: Long) =
        db.portfolioDao.deleteTransaction(transactionId)

    override suspend fun clearPortfolio() = db.portfolioDao.clearEntirePortfolio()

    override suspend fun deleteAssetByCoinId(coinId: String) =
        db.portfolioDao.deleteTransactionsByCoinId(coinId)

    override fun listenTransactionsByCoinId(coinId: String): Flow<List<PortfolioTransaction>> {
        return db.portfolioDao.listenTransactionsByCoin(coinId)
            .map { transaction ->
                transaction.map { it.toPortfolioTransaction() }
            }
    }
}