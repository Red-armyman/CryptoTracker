package ru.my.cryptotracker.ui.screens.portfolio

import ru.my.cryptotracker.core.ui.text.UiText

sealed interface PortfolioEffect {
    data class ShowSnackbar(val message: UiText) : PortfolioEffect
    data class NavigateToGraph(val coinId: String) : PortfolioEffect
}


