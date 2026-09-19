package ru.my.cryptotracker.feature.dashboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.my.cryptotracker.core.ui.model.CoinUiModel
import ru.my.cryptotracker.feature.dashboard.ui.components.CoinRow
import ru.my.cryptotracker.feature.dashboard.ui.components.EmptyCoinsStub
import ru.my.cryptotracker.feature.dashboard.ui.components.OfflineDataBanner
import ru.my.cryptotracker.feature.dashboard.ui.components.SearchHeader
import timber.log.Timber

@Composable
fun CryptoDashboardScreen(
    onNavigateToGraph: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CryptoDashboardViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val coinsState by viewModel.coinsState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val showScrollToTopButton by remember { derivedStateOf { lazyListState.firstVisibleItemIndex > 0 } }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentContext = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        try {
            Timber.tag("CryptoNav")
                .d("LaunchedEffect: Корутина сбора событий успешно ЗАПУЩЕНА на ключе: $lifecycleOwner")
            viewModel.effectFlow
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { effect ->
                    when (effect) {
                        is CryptoDashboardEffect.ShowSnackbar -> {
                            Timber.tag("CryptoNav").d("LaunchedEffect: Показываем Snackbar.")
                            snackbarHostState.showSnackbar(
                                message = effect.message.asString(currentContext),
                                duration = SnackbarDuration.Short
                            )
                        }

                        is CryptoDashboardEffect.NavigateToGraph -> {
                            onNavigateToGraph(effect.coinId)
                        }
                    }
                }
        } finally {
            Timber.tag("CryptoNav")
                .d("LaunchedEffect: Сработал ЖЦ Compose. Корутина уничтожена без утечек памяти.")
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.startRefreshCoins()
        }
    }
    val sendIntent: (CryptoDashboardEvent) -> Unit = { intent -> viewModel.handleIntent(intent) }

    val onCoinClick = remember(sendIntent) {
        { coin: CoinUiModel -> sendIntent(CryptoDashboardEvent.CoinClicked(coin.id)) }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SearchHeader(
                    query = searchQuery,
                    onQueryChange = { sendIntent(CryptoDashboardEvent.SearchQueryChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val isFirstItemVisible = lazyListState.firstVisibleItemIndex == 0
                            val currentOffset = if (isFirstItemVisible) {
                                lazyListState.firstVisibleItemScrollOffset.toFloat()
                            } else {
                                300f
                            }
                            alpha = (1f - (currentOffset / 300f)).coerceIn(0f, 1f)
                            scaleX = (1f - (currentOffset / 1000f)).coerceIn(0.8f, 1f)
                            scaleY = (1f - (currentOffset / 1000f)).coerceIn(0.8f, 1f)
                        }
                )
            }

            when (val currentCoins = coinsState) {

                is CryptoDashboardUiState.Loading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is CryptoDashboardUiState.Error -> item {
                    Text(
                        text = currentCoins.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is CryptoDashboardUiState.Success -> {
                    item {
                        AnimatedVisibility(
                            visible = currentCoins.isOffline,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            OfflineDataBanner(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    if (currentCoins.coinsList.isEmpty()) {
                        item { EmptyCoinsStub(modifier = Modifier.fillMaxWidth()) }
                    } else {
                        items(items = currentCoins.coinsList, key = { it.id }) { coinModel ->
                            CoinRow(
                                coinModel = coinModel,
                                onClick = onCoinClick
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showScrollToTopButton,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { scope.launch { lazyListState.animateScrollToItem(0) } },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}