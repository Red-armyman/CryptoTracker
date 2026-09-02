package ru.my.cryptotracker.model.mappers

import ru.my.cryptotracker.core.model.PortfolioAsset
import ru.my.cryptotracker.model.entities.PortfolioAssetUiModel


fun PortfolioAsset.toPortfolioAssetUiModel(): PortfolioAssetUiModel {
    return PortfolioAssetUiModel(
        coinId = coinId,
        displayTicker = displayTicker,
        imageUrl = imageUrl,
        totalAmount = totalAmount,
        currentPriceUsd = currentPriceUsd,
        marketValueUsd = marketValueUsd,
        averagePurchasePrice = averagePurchasePrice,
        assetProfitLossUsd = profitLossUsd,
        assetProfitLossPercent = profitLossPercent
    )
}