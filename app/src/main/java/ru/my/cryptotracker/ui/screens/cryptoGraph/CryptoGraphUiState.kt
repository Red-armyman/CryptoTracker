package ru.my.cryptotracker.ui.screens.cryptoGraph

data class CryptoGraphUiState(
    val coinId: String = "",
    val graphState: GraphUiState = GraphUiState.Loading(),
)