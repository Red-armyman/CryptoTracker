package ru.my.cryptotracker.feature.portfolio.screens.portfolio.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.my.cryptotracker.feature.portfolio.R
import ru.my.cryptotracker.core.designsystem.theme.CryptoTrackerTheme
import java.util.Locale

@Composable
fun PortfolioHeader(
    totalBalance: Double,
    profitLossUsd: Double,
    profitLossPercent: Double,
    isBalanceHidden: Boolean,
    modifier: Modifier = Modifier
) {
    val isPositive = profitLossUsd >= 0.0
    val pnlSign = if (isPositive) "+" else ""
    val pnlColor = if (isPositive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.portfolio_header_total_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isBalanceHidden) {
                stringResource(R.string.portfolio_hidden_balance)
            } else {
                "$${"%.2f".format(Locale.US, totalBalance)}"
            },
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isBalanceHidden) {
                stringResource(R.string.portfolio_hidden_pnl)
            } else {
                "$pnlSign$${"%.2f".format(Locale.US, profitLossUsd)} ($pnlSign${"%.2f".format(Locale.US, profitLossPercent)}%)"
            },
            style = MaterialTheme.typography.titleMedium,
            color = pnlColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@PreviewLightDark
@Composable
fun PortfolioHeaderProfitPreview() {
    CryptoTrackerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PortfolioHeader(
                totalBalance = 12450.85,      // Общий баланс кошелька
                profitLossUsd = 1205.40,      // Абсолютный профит в баксах
                profitLossPercent = 10.74,    // Относительный ROI в процентах
                isBalanceHidden = false       // Скрываем данные
            )
        }
    }
}