package ru.my.cryptotracker.ui.screens.cryptoDashboard

import ru.my.cryptotracker.model.entities.CoinUiModel
import kotlinx.collections.immutable.ImmutableList

sealed interface CryptoDashboardUiState {
    object Loading : CryptoDashboardUiState
    data class Success(
        val coinsList: ImmutableList<CoinUiModel>,
        val isOffline: Boolean = false,
    ) : CryptoDashboardUiState

    data class Error(val errorMessage: String) : CryptoDashboardUiState
}