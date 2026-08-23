package ru.my.cryptotracker.ui.screens.cryptoDashboard

sealed interface CryptoDashboardEvent {
    data class SearchQueryChanged(val query: String) : CryptoDashboardEvent
    data class CoinClicked(val coinId: String) : CryptoDashboardEvent
}