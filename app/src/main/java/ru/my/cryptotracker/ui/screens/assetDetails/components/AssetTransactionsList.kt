package ru.my.cryptotracker.ui.screens.assetDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.model.entities.PortfolioTransaction
import ru.my.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun AssetTransactionsList(
    transactions: ImmutableList<PortfolioTransaction>,
    coinId: String,
    onAction: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = transactions,
            key = { it.id }
        ) { tx ->

            AssetTransactionItem(
                transaction = tx,
                coinId = coinId,
                onAction = onAction
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AssetTransactionItemPreview() {
    CryptoTrackerTheme {
        Surface {
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