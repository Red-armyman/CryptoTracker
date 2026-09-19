package ru.my.cryptotracker.core.model

data class SecurePreferences(
    val totalBalanceUsd: Double = 0.0,
    val isBalanceHidden: Boolean = false,
    val activeCurrency: String = "USD",
    val lastSyncTimestamp: Long = 0L,
)