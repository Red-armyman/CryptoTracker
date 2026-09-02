package ru.my.cryptotracker.core.data.mappers

import ru.my.cryptotracker.core.model.MarketCoin
import ru.my.cryptotracker.core.model.database.entity.CoinDb
import ru.my.cryptotracker.core.data.network.dto.CoinDto

fun CoinDto.toCoinDb(): CoinDb {
    return CoinDb(
        id = this.id,
        symbol = this.symbol,
        currentPrice = this.currentPrice,
        imageUrl = this.imageUrl
    )
}

fun List<CoinDto>.toCoinDbList(): List<CoinDb> {
    return this.map { it.toCoinDb() }
}

fun List<CoinDb>.toMarketCoinList(): List<MarketCoin> {
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