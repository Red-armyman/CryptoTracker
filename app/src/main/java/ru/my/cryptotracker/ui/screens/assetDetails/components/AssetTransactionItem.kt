package ru.my.cryptotracker.ui.screens.assetDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.my.cryptotracker.R
import ru.my.cryptotracker.core.model.PortfolioTransaction
import ru.my.cryptotracker.core.designsystem.theme.CryptoTrackerTheme

@Composable
fun AssetTransactionItem(
    transaction: PortfolioTransaction,
    coinId: String,
    onAction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onAction(transaction.id)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val isSwiping =
                dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart

            if (isSwiping) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val currentOffset = dismissState.requireOffset()
                            val totalWidth = size.width
                            val threshold = totalWidth * 0.25f
                            val absoluteOffset =
                                kotlin.math.abs(currentOffset)

                            alpha =
                                if (absoluteOffset >= threshold) 1f else 0f
                        }
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.graphicsLayer {
                            val currentOffset = dismissState.requireOffset()
                            val totalWidth = size.width
                            val thirdOfScreen = totalWidth * 0.33f
                            val absoluteOffset =
                                kotlin.math.abs(currentOffset)

                            if (absoluteOffset >= thirdOfScreen) {
                                val remainingDistance =
                                    totalWidth - thirdOfScreen
                                val progressAfterThird =
                                    ((absoluteOffset - thirdOfScreen) / remainingDistance).coerceIn(
                                        0f,
                                        1f
                                    )

                                translationX =
                                    (1f - progressAfterThird) * 40.dp.toPx()
                                alpha = progressAfterThird
                            } else {
                                alpha = 0f
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.portfolio_action_delete),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize())
            }
        },
        content = {
            AssetTransactionCard(transaction, coinId)
        }
    )
}

@PreviewLightDark
@Composable
private fun AssetTransactionItemPreview() {
    CryptoTrackerTheme {
        Surface  {
            AssetTransactionItem(
                transaction = PortfolioTransaction(
                    id = 42L,
                    coinId = "ethereum",
                    amount = 1.845,
                    purchasePrice = 3120.00,
                    timestamp = 1785500000000L
                ),
                coinId = "eth",
                onAction = {  }
            )
        }
    }
}