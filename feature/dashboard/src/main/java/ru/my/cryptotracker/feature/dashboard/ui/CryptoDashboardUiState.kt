package ru.my.cryptotracker.feature.dashboard.ui

import ru.my.cryptotracker.core.ui.model.CoinUiModel
import kotlinx.collections.immutable.ImmutableList

sealed interface CryptoDashboardUiState {
    object Loading : CryptoDashboardUiState
    data class Success(
        val coinsList: ImmutableList<CoinUiModel>,
        val isOffline: Boolean = false,
    ) : CryptoDashboardUiState

    data class Error(val errorMessage: String) : CryptoDashboardUiState
}