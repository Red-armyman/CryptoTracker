package ru.my.cryptotracker.feature.dashboard.ui

import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.core.ui.model.CoinUiModel

sealed interface CoinsAction {
    data class CacheUpdated(
        val cachedCoins: ImmutableList<CoinUiModel>,
    ) : CoinsAction

    data class NetworkErrorOccurred(val message: String) : CoinsAction
}