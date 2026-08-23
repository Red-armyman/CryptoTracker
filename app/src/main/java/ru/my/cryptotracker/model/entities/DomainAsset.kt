package ru.my.cryptotracker.model.entities

/**
 * Хранит чистую свернутую математику по конкретной монете.
 */
data class DomainAsset(
    val coinId: String,
    val totalAmount: Double,        // Сумма всех покупок минус продажи
    val totalInvestmentUsd: Double,  // Сколько физически баксов потрачено на этот актив
    val averagePurchasePrice: Double // Вычисленная средняя цена входа
)