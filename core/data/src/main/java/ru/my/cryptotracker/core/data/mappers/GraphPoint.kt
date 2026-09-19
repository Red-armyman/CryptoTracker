package ru.my.cryptotracker.core.data.mappers

import ru.my.cryptotracker.core.model.GraphPoint

fun List<Double>.toGraphPoint(): GraphPoint {
    return GraphPoint(
        timestamp = this.getOrNull(0)?.toLong() ?: 0L,
        price = this.getOrNull(1) ?: 0.0
    )
}

fun List<List<Double>>.toGraphPointsList(): List<GraphPoint> {
    return this.map { innerList -> innerList.toGraphPoint() }
}