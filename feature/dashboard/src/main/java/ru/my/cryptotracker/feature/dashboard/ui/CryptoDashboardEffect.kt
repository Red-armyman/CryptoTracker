package ru.my.cryptotracker.feature.dashboard.ui

import ru.my.cryptotracker.core.ui.text.UiText

sealed interface CryptoDashboardEffect {
    data class ShowSnackbar(val message: UiText) : CryptoDashboardEffect
    data class NavigateToGraph(val coinId: String) : CryptoDashboardEffect
}