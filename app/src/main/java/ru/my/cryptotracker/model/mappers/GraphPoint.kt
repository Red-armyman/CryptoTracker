package ru.my.cryptotracker.model.mappers

import ru.my.cryptotracker.core.model.GraphPoint
import ru.my.cryptotracker.model.entities.GraphPointUiModel

fun GraphPoint.toUiModel(): GraphPointUiModel {
    return GraphPointUiModel(
        price = this.price.toFloat(),
        displayPrice = "$${this.price}",
        timestamp = this.timestamp
    )
}

fun List<GraphPoint>.toGraphUiModelsList(): List<GraphPointUiModel> {
    return this.map { it.toUiModel() }
}

fun List<Double>.toGraphPoint(): GraphPoint {
    return GraphPoint(
        timestamp = this.getOrNull(0)?.toLong() ?: 0L,
        price = this.getOrNull(1) ?: 0.0
    )
}

fun List<List<Double>>.toGraphPointsList(): List<GraphPoint> {
    return this.map { innerList -> innerList.toGraphPoint() }
}