package ru.my.cryptotracker.ui.screens.cryptoDashboard.reducer

import ru.my.cryptotracker.ui.screens.cryptoDashboard.CoinsAction
import ru.my.cryptotracker.ui.screens.cryptoDashboard.CryptoDashboardUiState

object CoinsReducer {

    /**
     * На основе старого состояния [ru.my.cryptotracker.ui.screens.cryptoDashboard.CryptoDashboardUiState] и действия [ru.my.cryptotracker.ui.screens.cryptoDashboard.CoinsAction]
     * вычисляет новое состояние экрана, сохраняя данные при сбоях сети!
     *
     *  Reducer используется намеренно для практики MVI.
     *  В текущем сценарии он избыточен, но позволяет явно отделить действия
     *  от переходов состояния.
     */
    fun reduce(
        oldState: CryptoDashboardUiState,
        action: CoinsAction
    ): CryptoDashboardUiState {
        return when (action) {

            is CoinsAction.CacheUpdated -> {
                CryptoDashboardUiState.Success(
                    coinsList = action.cachedCoins,
                    isOffline = false // База обновилась, всё штатно
                )
            }

            is CoinsAction.NetworkErrorOccurred -> {
                (oldState as? CryptoDashboardUiState.Success)?.copy(isOffline = true)
                    ?: CryptoDashboardUiState.Error(errorMessage = action.message)
            }
        }
    }
}