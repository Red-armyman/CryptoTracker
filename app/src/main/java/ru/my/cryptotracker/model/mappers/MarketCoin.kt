package ru.my.cryptotracker.model.mappers

import ru.my.cryptotracker.model.entities.CoinUiModel
import ru.my.cryptotracker.model.entities.MarketCoin

fun MarketCoin.toUiModel(): CoinUiModel {
    return CoinUiModel(
        id = id,
        displayTicker = symbol.uppercase(),
        displayPrice = "$$priceUsd",
        imageURL = imageUrl
    )
}