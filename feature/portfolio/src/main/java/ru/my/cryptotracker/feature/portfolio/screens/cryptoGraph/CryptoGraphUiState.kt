package ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph

data class CryptoGraphUiState(
    val coinId: String = "",
    val graphState: GraphUiState = GraphUiState.Loading(),
)