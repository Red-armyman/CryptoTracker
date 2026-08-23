package ru.my.cryptotracker.ui.screens.portfolio.components

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Хранит уже рассчитанные тригонометрические градусы для фазы Draw.
 */
@Immutable
data class PieSegment(
    val coinId: String,
    val ticker: String,
    val startAngle: Float, // Точный градус старта дуги в круге
    val sweepAngle: Float, // Размер дуги сектора в градусах (доля от 360)
    val color: Color       // Уникальный цвет монеты в диаграмме
)