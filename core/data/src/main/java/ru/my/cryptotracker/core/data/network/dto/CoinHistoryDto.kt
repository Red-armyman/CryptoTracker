package ru.my.cryptotracker.core.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinHistoryDto(
    @SerialName("prices") val prices: List<List<Double>> // Стримы вида [[timestamp, price], [timestamp, price]]
)
