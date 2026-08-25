package ru.my.cryptotracker.model.mappers

import ru.my.cryptotracker.core.model.MarketCoin
import ru.my.cryptotracker.model.entities.CoinUiModel

fun MarketCoin.toUiModel(): CoinUiModel {
    return CoinUiModel(
        id = id,
        displayTicker = symbol.uppercase(),
        displayPrice = "$$priceUsd",
        imageURL = imageUrl
    )
}