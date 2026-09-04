package ru.my.cryptotracker.feature.dashboard.ui

sealed interface CryptoDashboardEvent {
    data class SearchQueryChanged(val query: String) : CryptoDashboardEvent
    data class CoinClicked(val coinId: String) : CryptoDashboardEvent
}