package ru.my.cryptotracker.ui.screens.portfolio

import ru.my.cryptotracker.ui.util.UiText

sealed interface PortfolioAction {
    data class NewStateArrived(val successState: PortfolioUiState.Success) : PortfolioAction
    data class ErrorOccurred(val message: UiText) : PortfolioAction
    object ExecutionStarted : PortfolioAction
}