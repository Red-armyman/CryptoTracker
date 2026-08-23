package ru.my.cryptotracker.model.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "portfolio_transactions")
data class PortfolioDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L, // Автогенерация ID для каждой уникальной сделки
    val coinId: String, // Идентификатор монеты из CoinGecko (например, "bitcoin", "solana")
    val amount: Double, // Количество купленных/проданных монет (например, 0.05 BTC)
    val purchasePrice: Double, // Цена монеты на момент совершения сделки в долларах
    val timestamp: Long // Точное системное время совершения сделки (для исторического таймлайна)
)