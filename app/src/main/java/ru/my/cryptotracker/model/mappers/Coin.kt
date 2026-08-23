package ru.my.cryptotracker.model.mappers

import ru.my.cryptotracker.model.entities.CoinUiModel
import ru.my.cryptotracker.model.entities.MarketCoin
import ru.my.cryptotracker.model.database.entity.CoinDb
import ru.my.cryptotracker.model.network.dto.CoinDto

fun CoinDb.toUiModel(): CoinUiModel {
    return CoinUiModel(
        id = this.id,
        displayTicker = this.symbol.uppercase(),
        displayPrice = "$${this.currentPrice}",
        imageURL = this.imageUrl
    )
}

fun List<CoinDb>.toUiModelsList(): List<CoinUiModel> {
    return this.map { it.toUiModel() }
}


fun CoinDto.toDbEntity(): CoinDb {
    return CoinDb(
        id = this.id,
        symbol = this.symbol,
        currentPrice = this.currentPrice,
        imageUrl = this.imageUrl
    )
}

fun List<CoinDto>.toDbEntitiesList(): List<CoinDb> {
    return this.map { it.toDbEntity() }
}

fun List<CoinDb>.toMarketCoins(): List<MarketCoin> {
    return map { it.toMarketCoin() }
}

fun CoinDb.toMarketCoin(): MarketCoin {
    return MarketCoin(
        id = id,
        symbol = symbol,
        priceUsd = currentPrice,
        imageUrl = imageUrl
    )
}