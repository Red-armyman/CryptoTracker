package ru.my.cryptotracker.core.model.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "cached_coins")
data class CoinDb(
    @PrimaryKey val id: String,
    val symbol: String,
    val currentPrice: Double,
    @ColumnInfo(defaultValue = "")
    val imageUrl: String = ""
)
