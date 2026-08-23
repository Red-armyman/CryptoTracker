package ru.my.cryptotracker.model.entities

data class PortfolioTransaction(
    val id: Long,
    val coinId: String,
    val amount: Double,
    val purchasePrice: Double,
    val timestamp: Long
)