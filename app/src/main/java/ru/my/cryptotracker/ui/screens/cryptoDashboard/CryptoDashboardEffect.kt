package ru.my.cryptotracker.ui.screens.cryptoDashboard

import ru.my.cryptotracker.ui.util.UiText

sealed interface CryptoDashboardEffect {
    data class ShowSnackbar(val message: UiText) : CryptoDashboardEffect
    data class NavigateToGraph(val coinId: String) : CryptoDashboardEffect
}