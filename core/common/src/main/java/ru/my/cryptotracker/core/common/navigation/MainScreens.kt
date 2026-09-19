package ru.my.cryptotracker.core.common.navigation

import kotlinx.serialization.Serializable

sealed interface MainScreens {

    // Экран глобального дашборда котировок (Маркет)
    @Serializable
    object Dashboard : MainScreens

    // Новый интерактивный экран кошелька пользователя
    @Serializable
    object Portfolio : MainScreens

    // Экран детального интерактивного графика конкретной монеты.
    @Serializable
    data class CryptoGraph(val coinId: String) : MainScreens

    @Serializable
    data class AssetDetails(val coinId: String) : MainScreens
}