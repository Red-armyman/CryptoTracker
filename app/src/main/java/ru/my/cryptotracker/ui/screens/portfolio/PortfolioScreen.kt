package ru.my.cryptotracker.ui.screens.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import ru.my.cryptotracker.R
import ru.my.cryptotracker.model.entities.PortfolioAssetUiModel
import ru.my.cryptotracker.ui.screens.portfolio.components.AddTransactionBottomSheet
import ru.my.cryptotracker.ui.screens.portfolio.components.AssetRow
import ru.my.cryptotracker.ui.screens.portfolio.components.EmptyPortfolioStub
import ru.my.cryptotracker.ui.screens.portfolio.components.PortfolioHeader
import ru.my.cryptotracker.ui.theme.CryptoTrackerTheme
import timber.log.Timber
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    onNavigateToGraph: (String) -> Unit,
    onNavigateToTransactionHistory: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val currentContext = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            Timber.tag("CryptoNav")
                .d("LaunchedEffect [Портфель]: Корутина портфеля ЗАПУЩЕНА'")
            viewModel.effectFlow.collect { effect ->
                when (effect) {
                    is PortfolioEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message.asString(
                                currentContext
                            ), duration = SnackbarDuration.Short
                        )
                    }

                    is PortfolioEffect.NavigateToGraph -> {
                        onNavigateToGraph(effect.coinId)
                    }
                }
            }
        } finally {
            Timber.tag("CryptoNav")
                .d("LaunchedEffect: Сработал ЖЦ Compose. Корутина уничтожена без утечек памяти.")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.portfolio_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    (uiState as? PortfolioUiState.Success)?.let { successState ->

                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }


                        IconButton(onClick = { viewModel.handleIntent(PortfolioEvent.ToggleBalanceVisibility) }) {
                            Icon(
                                imageVector = if (successState.isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.handleIntent(PortfolioEvent.ClearPortfolio) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is PortfolioUiState.Waiting -> Text(stringResource(R.string.portfolio_waiting_initialization))
                is PortfolioUiState.Loading -> CircularProgressIndicator()
                is PortfolioUiState.Error -> Text(
                    text = state.errorMessage.asString(),
                    color = MaterialTheme.colorScheme.error
                )

                is PortfolioUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PortfolioHeader(
                            totalBalance = state.overview.totalBalanceUsd,
                            profitLossUsd = state.overview.totalProfitLossUsd,
                            profitLossPercent = state.overview.totalProfitLossPercent,
                            isBalanceHidden = state.isBalanceHidden,
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        )
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                PortfolioPieChart(
                                    assets = state.assets,
                                    totalBalanceUsd = state.overview.totalBalanceUsd
                                )
                            }

                            if (state.assets.isEmpty()) {
                                item {
                                    EmptyPortfolioStub(onAddTransactionClick = {
                                        showBottomSheet = true
                                    })
                                }
                            } else {
                                items(
                                    items = state.assets,
                                    key = { it.coinId }
                                ) { assetModel ->
                                    val dismissState = rememberSwipeToDismissBoxState()

                                    LaunchedEffect(dismissState.currentValue) {
                                        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                            viewModel.handleIntent(
                                                PortfolioEvent.DeleteAsset(
                                                    assetModel.coinId
                                                )
                                            )
                                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                        }
                                    }

                                    SwipeToDismissBox(
                                        state = dismissState,
                                        enableDismissFromStartToEnd = false,
                                        enableDismissFromEndToStart = true,
                                        backgroundContent = {
                                            // Проверяем, что идет свайп влево
                                            val isSwiping =
                                                dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart

                                            if (isSwiping) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .graphicsLayer {
                                                            // 1. Безопасно получаем текущий сдвиг карточки в пикселях (он отрицательный при свайпе влево)
                                                            val currentOffset =
                                                                dismissState.requireOffset()
                                                            // 2. Получаем полную ширину контейнера
                                                            val totalWidth = size.width

                                                            // 3. Высчитываем порог в 1/3 эана (делаем его положительным для сравнения)
                                                            val threshold = totalWidth * 0.33f
                                                            val absoluteOffset = abs(currentOffset)

                                                            // 4. Включаем подложку ТОЛЬКО когда пройдена треть экрана
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
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            8.dp
                                                        ),
                                                        modifier = Modifier.graphicsLayer {
                                                            // Текст анимируем тоже только после 1/3 экрана
                                                            val currentOffset =
                                                                dismissState.requireOffset()
                                                            val totalWidth = size.width
                                                            val thirdOfScreen = totalWidth * 0.33f
                                                            val absoluteOffset = abs(currentOffset)

                                                            if (absoluteOffset >= thirdOfScreen) {
                                                                // Рассчитываем локальный прогресс от 1/3 до конца экрана для плавного выезда текста
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
                                        }
                                    ) {
                                        AssetRow(
                                            asset = assetModel,
                                            isBalanceHidden = state.isBalanceHidden,
                                            onNavigateToGraph = {
                                                viewModel.handleIntent(
                                                    PortfolioEvent.AssetClicked(
                                                        assetModel.coinId
                                                    )
                                                )
                                            },

                                            onNavigateToDetails = { coinId ->
                                                onNavigateToTransactionHistory(coinId)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showBottomSheet && uiState is PortfolioUiState.Success) {
        val successState = uiState as PortfolioUiState.Success

        AddTransactionBottomSheet(
            availableCoins = successState.availableMarketCoins,
            onDismiss = { showBottomSheet = false },
            onSaveTransaction = { coinId, amount, price ->
                viewModel.handleIntent(
                    PortfolioEvent.AddTransaction(
                        coinId = coinId,
                        amount = amount,
                        purchasePrice = price
                    )
                )
                showBottomSheet = false
            }
        )
    }
}

@PreviewLightDark
@Composable
fun PortfolioHeaderLossPreview() {
    CryptoTrackerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PortfolioHeader(
                totalBalance = 8450.00,
                profitLossUsd = -450.50,
                profitLossPercent = -5.06,
                isBalanceHidden = false,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PortfolioScreenPreview() {
    CryptoTrackerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    PortfolioHeader(
                        totalBalance = 12450.85,
                        profitLossUsd = 1205.40,
                        profitLossPercent = 10.74,
                        isBalanceHidden = true,
                    )
                }

                items(
                    items = persistentListOf(
                        PortfolioAssetUiModel(
                            coinId = "bitcoin",
                            displayTicker = "BTC",
                            imageUrl = "",
                            totalAmount = 0.15,
                            currentPriceUsd = 65000.0,
                            marketValueUsd = 9750.0,
                            averagePurchasePrice = 60000.0,
                            assetProfitLossUsd = 750.0,
                            assetProfitLossPercent = 8.33
                        ),
                        PortfolioAssetUiModel(
                            coinId = "solana",
                            displayTicker = "SOL",
                            imageUrl = "",
                            totalAmount = 15.0,
                            currentPriceUsd = 180.0,
                            marketValueUsd = 2700.0,
                            averagePurchasePrice = 150.0,
                            assetProfitLossUsd = 450.0,
                            assetProfitLossPercent = 20.0
                        )
                    )
                ) { assetModel ->
                    AssetRow(
                        asset = assetModel,
                        onNavigateToGraph = {},
                        onNavigateToDetails = {},
                        isBalanceHidden = true,
                    )
                }
            }
        }
    }
}