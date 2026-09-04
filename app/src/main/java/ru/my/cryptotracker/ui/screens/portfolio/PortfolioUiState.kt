package ru.my.cryptotracker.ui.screens.portfolio

import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.core.model.PortfolioOverview
import ru.my.cryptotracker.core.ui.model.CoinUiModel
import ru.my.cryptotracker.model.entities.PortfolioAssetUiModel
import ru.my.cryptotracker.core.ui.text.UiText

sealed interface PortfolioUiState {

    // Начальное ожидание инициализации потоков слияния данных (Stream Fusion)
    object Waiting : PortfolioUiState

    // Фаза первичной загрузки транзакций и котировок монет из SQLite
    object Loading : PortfolioUiState

    data class Success(
        val overview: PortfolioOverview,                      // Все данные портфеля, рассчитанные в UseCase
        val availableMarketCoins: ImmutableList<CoinUiModel>, // Все доступные монеты для выпадающего списка покупки
        val assets: ImmutableList<PortfolioAssetUiModel>,     // Список купленных монет пользователя
        val isBalanceHidden: Boolean = false                  // Скрывать данные портфеля
    ) : PortfolioUiState

    data class Error(val errorMessage: UiText) : PortfolioUiState
}
