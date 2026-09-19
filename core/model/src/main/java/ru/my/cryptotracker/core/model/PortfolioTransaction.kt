package ru.my.cryptotracker.core.model

data class PortfolioTransaction(
    val id: Long,
    val coinId: String,
    val amount: Double,
    val purchasePrice: Double,
    val timestamp: Long
)