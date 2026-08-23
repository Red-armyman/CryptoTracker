package ru.my.cryptotracker.model.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("current_price") val currentPrice: Double,
    @SerialName("symbol") val symbol: String,
    @SerialName("image") val imageUrl: String
)