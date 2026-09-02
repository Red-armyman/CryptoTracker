package ru.my.cryptotracker.model.mappers

import ru.my.cryptotracker.core.model.GraphPoint
import ru.my.cryptotracker.model.entities.GraphPointUiModel

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