package ru.my.cryptotracker.core.model

/**
 * Результат расчёта портфеля и данные,
 * необходимые для его дальнейшего отображения.
 */
data class PortfolioOverview(
    val assets: List<PortfolioAsset>,           // Рассчитанные активы портфеля
    val marketCoins: List<MarketCoin>,          // Актуальные данные рыночных монет
    val totalBalanceUsd: Double,                // Общая текущая стоимость портфеля
    val totalProfitLossUsd: Double,             // Общая прибыль/убыток в долларах
    val totalProfitLossPercent: Double,         // Общая прибыль/убыток в процентах
    val isOffline: Boolean,                     // Котировки получены из устаревшего кэша
)