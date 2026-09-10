package ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.my.cryptotracker.feature.portfolio.R
import ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph.GraphUiState


@Composable
fun AnimatedGraphContent(
    getGraphState: () -> GraphUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val graphState = getGraphState()) {

            is GraphUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            is GraphUiState.Success -> {
                val pointsList = graphState.points
                if (pointsList.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.graph_state_empty),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    val graphData = remember(pointsList) {
                        val maxPrice = pointsList.maxOf { it.price }
                        val minPrice = pointsList.minOf { it.price }
                        val priceRange = (maxPrice - minPrice).coerceAtLeast(0.01f)
                        Triple(maxPrice, minPrice, priceRange)
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(8.dp)
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val (_, minPrice, priceRange) = graphData
                            val distanceX = canvasWidth / (pointsList.size - 1).coerceAtLeast(1)

                            val graphPath = Path().apply {
                                pointsList.forEachIndexed { index, point ->
                                    val x = index * distanceX
                                    val normalizedPrice = (point.price - minPrice) / priceRange
                                    val y = (canvasHeight - (normalizedPrice * canvasHeight))
                                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                                }
                            }

                            drawPath(
                                path = graphPath,
                                color = Color(0xFF4CAF50),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                }
            }

            is GraphUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = graphState.message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onRetryClick
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.graph_btn_retry),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}