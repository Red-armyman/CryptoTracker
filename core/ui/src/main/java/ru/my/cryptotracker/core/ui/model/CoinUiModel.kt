package ru.my.cryptotracker.core.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class CoinUiModel(
    val id: String,
    val displayTicker: String,
    val displayPrice: String,
    val imageURL: String = ""
)