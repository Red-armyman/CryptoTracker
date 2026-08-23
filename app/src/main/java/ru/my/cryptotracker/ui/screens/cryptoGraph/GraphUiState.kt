package ru.my.cryptotracker.ui.screens.cryptoGraph

import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.model.entities.GraphPointUiModel

sealed interface GraphUiState {
    class Loading : GraphUiState

    data class Success(val points: ImmutableList<GraphPointUiModel>) :
        GraphUiState

    data class Error(val message: String) : GraphUiState
}