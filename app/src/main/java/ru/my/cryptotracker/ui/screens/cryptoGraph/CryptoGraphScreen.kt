package ru.my.cryptotracker.ui.screens.cryptoGraph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import ru.my.cryptotracker.R
import ru.my.cryptotracker.ui.screens.cryptoGraph.components.AnimatedGraphContent
import ru.my.cryptotracker.ui.screens.cryptoGraph.components.GraphLiveTickerCard
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CryptoGraphScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CryptoGraphViewModel = hiltViewModel(),
) {
    val stateHolder = viewModel.uiState.collectAsStateWithLifecycle()
    val coinId = viewModel.coinId

    LaunchedEffect(Unit) {
        try {
            Timber.tag("CryptoNav")
                .d("LaunchedEffect [График]: Корутина аналитики ЗАПУЩЕНА для монеты: '$coinId'")
            delay(Long.MAX_VALUE.milliseconds)
        } finally {
            Timber.tag("CryptoNav")
                .d("LaunchedEffect [График]: Корутина аналитики монеты '$coinId' успешно УНИЧТОЖЕНА.")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val formattedCoinName = remember(coinId) { coinId.replaceFirstChar { it.uppercase() } }

        Text(
            text = stringResource(id = R.string.graph_screen_title, formattedCoinName),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        GraphLiveTickerCard(
            getGraphState = { stateHolder.value.graphState }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedGraphContent(
            getGraphState = { stateHolder.value.graphState },
            onRetryClick = { viewModel.retryLoading() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.graph_screen_btn_back))
        }
    }
}