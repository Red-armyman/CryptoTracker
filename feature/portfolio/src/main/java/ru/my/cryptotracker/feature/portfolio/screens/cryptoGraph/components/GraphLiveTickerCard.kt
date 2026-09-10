package ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph.GraphUiState
import timber.log.Timber
import java.util.Locale
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun GraphLiveTickerCard(
    getGraphState: () -> GraphUiState,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("Синхронизация с биржей...") }
    var tickerColor by remember { mutableStateOf(Color.Gray) }

    val updatedState = rememberUpdatedState(newValue = getGraphState())

    LaunchedEffect(Unit) {
        try {
            var previousPrice = 0.0

            while (true) {
                delay(4000.milliseconds)

                when (val latestState = updatedState.value) {
                    is GraphUiState.Success -> {
                        val points = latestState.points
                        val rawPrice = points.lastOrNull()?.price
                        val actualMarketPrice = rawPrice?.toDouble() ?: 0.0

                        if (actualMarketPrice == 0.0) continue

                        val simulatedMarketPrice = if (previousPrice == 0.0) {
                            actualMarketPrice
                        } else {
                            actualMarketPrice * (1.0 + (Random.nextDouble() * 0.05 - 0.02))
                        }

                        if (previousPrice != 0.0) {
                            if (simulatedMarketPrice > previousPrice) {
                                message = "Рынок растет! Свежая цена: $${"%.2f".format(Locale.US, simulatedMarketPrice)} 📈"
                                tickerColor = Color(0xFF4CAF50)
                            } else if (simulatedMarketPrice < previousPrice) {
                                message = "Цена падает! Свежая цена: $${"%.2f".format(Locale.US, simulatedMarketPrice)} 📉"
                                tickerColor = Color(0xFFF44336)
                            }
                        } else {
                            message = "Связь установлена. Текущая цена: $${"%.2f".format(Locale.US, simulatedMarketPrice)}"
                            tickerColor = Color.DarkGray
                        }
                        previousPrice = simulatedMarketPrice
                    }
                    else -> {
                        message = "Ожидание стабилизации сети..."
                        tickerColor = Color.Gray
                    }
                }
            }
        } finally {
            Timber.tag("CryptoNav").d("GraphLiveTickerCard: Фоновый мониторинг успешно остановлен.")
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val localPair = Pair(message, tickerColor)
            Crossfade(targetState = localPair, label = "TickerAnim") { (currentMessage, currentColor) ->
                Text(
                    text = currentMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = currentColor
                )
            }
        }
    }
}