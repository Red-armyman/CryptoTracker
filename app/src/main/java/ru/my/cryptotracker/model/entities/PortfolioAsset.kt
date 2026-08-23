package ru.my.cryptotracker.model.entities

/**
 * Результат расчёта стоимости и прибыли/убытка
 * конкретного актива в портфеле.
 */
data class PortfolioAsset(
    val coinId: String,                    // Уникальный идентификатор монеты
    val totalAmount: Double,               // Общее количество монеты в портфеле
    val currentPriceUsd: Double,           // Текущая цена монеты в долларах США
    val marketValueUsd: Double,            // Текущая стоимость актива
    val averagePurchasePrice: Double,      // Средняя цена покупки
    val profitLossUsd: Double,             // Прибыль/убыток в долларах США
    val profitLossPercent: Double,         // Прибыль/убыток в процентах
    val displayTicker: String,             // Тикер монеты для отображения на экране
    val imageUrl: String,                  // Ссылка на изображение монеты
)
