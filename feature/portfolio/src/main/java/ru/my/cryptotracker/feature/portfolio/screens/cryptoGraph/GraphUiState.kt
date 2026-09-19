package ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph

import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.feature.portfolio.model.GraphPointUiModel

sealed interface GraphUiState {
    class Loading : GraphUiState

    data class Success(val points: ImmutableList<GraphPointUiModel>) :
        GraphUiState

    data class Error(val message: String) : GraphUiState
}