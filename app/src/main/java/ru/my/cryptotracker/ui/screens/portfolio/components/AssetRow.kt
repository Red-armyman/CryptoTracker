package ru.my.cryptotracker.ui.screens.portfolio.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.my.cryptotracker.R
import ru.my.cryptotracker.model.entities.PortfolioAssetUiModel
import ru.my.cryptotracker.ui.theme.CryptoTrackerTheme
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AssetRow(
    asset: PortfolioAssetUiModel,
    isBalanceHidden: Boolean,
    onNavigateToGraph: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = asset.assetProfitLossUsd >= 0.0
    val pnlSign = if (isPositive) "+" else ""
    val pnlColor = if (isPositive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onNavigateToGraph(asset.coinId) },
                onLongClick = { onNavigateToDetails(asset.coinId) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = asset.displayTicker.take(1),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.displayTicker,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBalanceHidden) {
                        stringResource(
                            R.string.portfolio_hidden_asset_amount,
                            asset.displayTicker
                        )
                    } else {
                        "${"%.2f".format(Locale.US,asset.totalAmount)} ${asset.displayTicker}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isBalanceHidden) {
                        stringResource(R.string.portfolio_hidden_balance)
                    } else {
                        "$${"%.2f".format(Locale.US, asset.marketValueUsd)}"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBalanceHidden) {
                        stringResource(R.string.portfolio_hidden_percent)
                    } else {
                        "$pnlSign${"%.2f".format(Locale.US, asset.assetProfitLossPercent)}%"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = pnlColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AssetRowPreview() {
    CryptoTrackerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AssetRow(
                asset = PortfolioAssetUiModel(
                    coinId = "bitcoin",
                    displayTicker = "BTC",
                    imageUrl = "",
                    totalAmount = 15.0,
                    currentPriceUsd = 65000.0,
                    marketValueUsd = 9750.0,
                    averagePurchasePrice = 60000.0,
                    assetProfitLossUsd = 750.0,
                    assetProfitLossPercent = 8.33
                ),
                onNavigateToGraph = {},
                onNavigateToDetails = {},
                isBalanceHidden = true,
            )
        }
    }
}
