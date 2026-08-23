package ru.my.cryptotracker.ui.screens.cryptoDashboard

import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.model.entities.CoinUiModel

sealed interface CoinsAction {
    data class CacheUpdated(
        val cachedCoins: ImmutableList<CoinUiModel>,
    ) : CoinsAction

    data class NetworkErrorOccurred(val message: String) : CoinsAction
}