package ru.my.cryptotracker.feature.dashboard.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.my.cryptotracker.core.designsystem.theme.CryptoTrackerTheme
import ru.my.cryptotracker.core.ui.model.CoinUiModel
import ru.my.cryptotracker.feature.dashboard.ui.utils.LogCompositionBranch

@Composable
fun CoinRow(
    coinModel: CoinUiModel,
    onClick: (CoinUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LogCompositionBranch("Строка монеты: ${coinModel.displayTicker}")

    Card(
        onClick = { onClick(coinModel) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoinImageFromURL(
                imageUrl = coinModel.imageURL,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = coinModel.displayTicker,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = coinModel.displayPrice,
                style = MaterialTheme.typography.bodyLarge
            )        }
    }
}

@PreviewLightDark
@Composable
private fun CoinRowPreview() {
    CryptoTrackerTheme {
        Surface {
            CoinRow(
                coinModel = CoinUiModel(
                    id = "eutbl",
                    imageURL = "https://coin-images.coingecko.com/coins/images/39657/large/EUTBL.png?1723517425",
                    displayTicker = "eutbl",
                    displayPrice = "$1.2"
                ),
                onClick = {}
            )
        }
    }
}