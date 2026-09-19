package ru.my.cryptotracker.feature.portfolio.screens.portfolio.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import androidx.compose.ui.tooling.preview.PreviewLightDark
import kotlinx.collections.immutable.persistentListOf
import ru.my.cryptotracker.feature.portfolio.model.PortfolioAssetUiModel

private val PieChartColors = listOf(
    Color(0xFF6200EE), // Фиолетовый (BTC)
    Color(0xFF03DAC6), // Бирюзовый (ETH)
    Color(0xFFFF9800), // Оранжевый (SOL)
    Color(0xFFE91E63), // Розовый (XRP)
    Color(0xFF4CAF50), // Зеленый (ADA)
    Color(0xFF2196F3)  // Синий (Остальные)
)

@Composable
fun PortfolioPieChart(
    assets: ImmutableList<PortfolioAssetUiModel>,
    totalBalanceUsd: Double,
    modifier: Modifier = Modifier
) {
    if (totalBalanceUsd <= 0.0 || assets.isEmpty()) return

    val segments = remember(assets, totalBalanceUsd) {
        var currentStartAngle = -90f // Стартуем ровно с верхней точки окружности (12 часов)

        assets.mapIndexed { index, asset ->
            val ratio = (asset.marketValueUsd / totalBalanceUsd).toFloat()
            val sweepAngle = ratio * 360f

            // Назначаем цвет из нашей палитры, если монет больше — пускаем по кругу через остаток от деления
            val color = PieChartColors[index % PieChartColors.size]

            val segment = PieSegment(
                coinId = asset.coinId,
                ticker = asset.displayTicker,
                startAngle = currentStartAngle,
                sweepAngle = sweepAngle,
                color = color
            )

            // Смещаем стартовый угол для следующего сектора на величину текущей дуги
            currentStartAngle += sweepAngle
            segment
        }
    }

    // Анимация плавного «накатывания» диаграммы при открытии экрана
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(assets) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000) // Плавно разворачиваем круг за 1 секунду
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .aspectRatio(1f)
        ) {
            val strokeWidth = 24.dp.toPx()

            segments.forEach { segment ->
                // Перемножаем дугу сектора на текущий прогресс анимации для эффекта живого разворачивания
                val animatedSweepAngle = segment.sweepAngle * animationProgress.value

                drawArc(
                    color = segment.color,
                    startAngle = segment.startAngle,
                    sweepAngle = animatedSweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PortfolioPieChartPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PortfolioPieChart(
                totalBalanceUsd = 10000.0,
                assets = persistentListOf(
                    PortfolioAssetUiModel(
                        coinId = "bitcoin", displayTicker = "BTC", imageUrl = "",
                        totalAmount = 0.1, currentPriceUsd = 60000.0, marketValueUsd = 6000.0,
                        averagePurchasePrice = 55000.0, assetProfitLossUsd = 500.0, assetProfitLossPercent = 9.0
                    ),
                    PortfolioAssetUiModel(
                        coinId = "ethereum", displayTicker = "ETH", imageUrl = "",
                        totalAmount = 1.0, currentPriceUsd = 30000.0, marketValueUsd = 3000.0,
                        averagePurchasePrice = 2800.0, assetProfitLossUsd = 200.0, assetProfitLossPercent = 7.14
                    ),
                    PortfolioAssetUiModel(
                        coinId = "solana", displayTicker = "SOL", imageUrl = "",
                        totalAmount = 5.0, currentPriceUsd = 200.0, marketValueUsd = 1000.0,
                        averagePurchasePrice = 180.0, assetProfitLossUsd = 100.0, assetProfitLossPercent = 11.1
                    )
                )
            )
        }
    }
}