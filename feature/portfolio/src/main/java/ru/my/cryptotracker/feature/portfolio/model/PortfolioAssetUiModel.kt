package ru.my.cryptotracker.feature.portfolio.model

/**
 * UI-МОДЕЛЬ МОНЕТЫ В ПОРТФЕЛЕ:
 */
data class PortfolioAssetUiModel(
    val coinId: String,          // "bitcoin"
    val displayTicker: String,   // "BTC"
    val imageUrl: String,        // Ссылка на иконку монеты для Coil
    val totalAmount: Double,     // Общее количество на руках (0.05)
    val currentPriceUsd: Double, // Текущая живая цена из кэша Дашборда ($65000.0)
    val marketValueUsd: Double,  // Текущая стоимость этого актива (0.05 * 65000 = $3250)
    val averagePurchasePrice: Double, // Средняя цена входа
    val assetProfitLossUsd: Double,   // Профит конкретно по этой монете в баксах
    val assetProfitLossPercent: Double // Профит конкретно по этой монете в процентах
)