package ru.my.cryptotracker.model.mappers


import ru.my.cryptotracker.core.model.DomainAsset
import ru.my.cryptotracker.core.model.PortfolioTransaction
import ru.my.cryptotracker.model.database.entity.PortfolioDb
import kotlin.collections.forEach

fun List<PortfolioDb>.toDomainAsset(coinId: String): DomainAsset {
    var totalAmount = 0.0
    var totalInvestment = 0.0

    forEach { tx ->
        totalAmount += tx.amount
        totalInvestment += (tx.amount * tx.purchasePrice)
    }

    val avgPrice = if (totalAmount > 0.0) totalInvestment / totalAmount else 0.0

    return DomainAsset(
        coinId = coinId,
        totalAmount = totalAmount,
        totalInvestmentUsd = totalInvestment,
        averagePurchasePrice = avgPrice
    )
}

fun PortfolioDb.toPortfolioTransaction() : PortfolioTransaction {
    return PortfolioTransaction(
        id = this.id,
        coinId = this.coinId,
        amount = this.amount,
        purchasePrice = this.purchasePrice,
        timestamp = this.timestamp
    )
}