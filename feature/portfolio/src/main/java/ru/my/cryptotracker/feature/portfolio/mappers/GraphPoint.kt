package ru.my.cryptotracker.feature.portfolio.mappers

import ru.my.cryptotracker.core.model.GraphPoint
import ru.my.cryptotracker.feature.portfolio.model.GraphPointUiModel

fun GraphPoint.toGraphPointUiModel(): GraphPointUiModel {
    return GraphPointUiModel(
        price = this.price.toFloat(),
        displayPrice = "$${this.price}",
        timestamp = this.timestamp
    )
}

fun List<GraphPoint>.toGraphPointUiModelList(): List<GraphPointUiModel> {
    return this.map { it.toGraphPointUiModel() }
}