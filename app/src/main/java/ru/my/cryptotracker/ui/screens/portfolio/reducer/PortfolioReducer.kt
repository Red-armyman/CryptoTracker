package ru.my.cryptotracker.ui.screens.portfolio.reducer

import ru.my.cryptotracker.ui.screens.portfolio.PortfolioAction
import ru.my.cryptotracker.ui.screens.portfolio.PortfolioUiState

object PortfolioReducer {

    fun reduce(
        oldState: PortfolioUiState,
        action: PortfolioAction
    ): PortfolioUiState {
        return when (action) {

            is PortfolioAction.NewStateArrived -> {
                action.successState
            }

            is PortfolioAction.ErrorOccurred -> {
                oldState as? PortfolioUiState.Success ?: PortfolioUiState.Error(errorMessage = action.message)
            }

            is PortfolioAction.ExecutionStarted -> {
                oldState as? PortfolioUiState.Success ?: PortfolioUiState.Loading
            }
        }
    }
}
