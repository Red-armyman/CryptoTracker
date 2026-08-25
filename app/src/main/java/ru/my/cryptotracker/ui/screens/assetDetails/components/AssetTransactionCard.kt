package ru.my.cryptotracker.ui.screens.assetDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.my.cryptotracker.R
import ru.my.cryptotracker.core.model.PortfolioTransaction
import ru.my.cryptotracker.ui.theme.CryptoTrackerTheme
import java.util.Locale

@Composable
fun AssetTransactionCard(
    transaction: PortfolioTransaction,
    coinId: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.4f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(

                    text = stringResource(
                        R.string.asset_details_amount,
                        "%.2f".format(Locale.US, transaction.amount),
                        coinId.uppercase()
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        R.string.asset_details_price,
                        "%.2f".format(Locale.US, transaction.purchasePrice)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = java.text.SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.US
                ).format(java.util.Date(transaction.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AssetTransactionCardPreview() {
    CryptoTrackerTheme {
        Surface {
            AssetTransactionCard(
                transaction = PortfolioTransaction(
                    id = 1L,
                    coinId = "bitcoin",
                    amount = 0.4572,
                    purchasePrice = 96450.50,
                    timestamp = 1785500000000L
                ),
                coinId = "btc"
            )
        }
    }
}