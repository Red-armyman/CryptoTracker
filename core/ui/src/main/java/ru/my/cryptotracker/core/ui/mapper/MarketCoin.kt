package ru.my.cryptotracker.core.ui.mapper

import ru.my.cryptotracker.core.model.MarketCoin
import ru.my.cryptotracker.core.ui.model.CoinUiModel

fun MarketCoin.toCoinUiModel(): CoinUiModel {
    return CoinUiModel(
        id = id,
        displayTicker = symbol.uppercase(),
        displayPrice = "$$priceUsd",
        imageURL = imageUrl
    )
}