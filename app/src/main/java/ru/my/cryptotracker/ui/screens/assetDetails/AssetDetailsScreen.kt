package ru.my.cryptotracker.ui.screens.assetDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ru.my.cryptotracker.R
import ru.my.cryptotracker.ui.screens.assetDetails.components.AssetTransactionsList
import ru.my.cryptotracker.ui.screens.portfolio.PortfolioEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AssetDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentContext = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collectLatest { effect ->
            when (effect) {
                is PortfolioEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message.asString(currentContext),
                        duration = SnackbarDuration.Short
                    )
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val cryptoName =
                        remember(viewModel.coinId) { viewModel.coinId.replaceFirstChar { it.uppercase() } }
                    Text(
                        stringResource(id = R.string.asset_details_title, cryptoName),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                is AssetDetailsUiState.Loading -> CircularProgressIndicator()
                is AssetDetailsUiState.Empty -> Text(
                    stringResource(id = R.string.asset_details_empty),
                    style = MaterialTheme.typography.bodyLarge
                )

                is AssetDetailsUiState.Error -> {
                    Text(
                        text = stringResource(R.string.asset_details_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is AssetDetailsUiState.Success -> {
                    AssetTransactionsList(
                        state.transactions,
                        coinId = state.coinId,
                        onAction = viewModel::deleteTransaction
                    )
                }
            }
        }
    }
}