package ru.my.cryptotracker.model.entities

import androidx.compose.runtime.Immutable

@Immutable
data class CoinUiModel(
    val id: String,
    val displayTicker: String,
    val displayPrice: String,
    val imageURL: String = ""
)