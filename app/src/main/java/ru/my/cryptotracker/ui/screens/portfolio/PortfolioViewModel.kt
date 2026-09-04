package ru.my.cryptotracker.ui.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.my.cryptotracker.R
import ru.my.cryptotracker.core.common.di.IODispatcher
import ru.my.cryptotracker.model.mappers.toPortfolioAssetUiModel
import ru.my.cryptotracker.core.ui.mapper.toCoinUiModel
import ru.my.cryptotracker.core.domain.security.SecurePreferencesRepository
import ru.my.cryptotracker.ui.screens.portfolio.reducer.PortfolioReducer
import ru.my.cryptotracker.core.ui.text.UiText
import ru.my.cryptotracker.usecase.portfolio.GetPortfolioOverviewUseCase
import ru.my.cryptotracker.usecase.portfolio.PortfolioUseCase
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    getPortfolioOverviewUseCase: GetPortfolioOverviewUseCase,
    private val portfolioUseCase: PortfolioUseCase,
    securityRepository: SecurePreferencesRepository,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _effectChannel = Channel<PortfolioEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effectFlow = _effectChannel.receiveAsFlow()

    val uiState: StateFlow<PortfolioUiState> = combine(
        getPortfolioOverviewUseCase(),
        securityRepository.securePreferences
    ) { overview, securePrefs ->

        val assets = overview.assets
            .map { it.toPortfolioAssetUiModel() }
            .toImmutableList()

        PortfolioReducer.reduce(
            oldState = PortfolioUiState.Loading,
            action = PortfolioAction.NewStateArrived(
                PortfolioUiState.Success(
                    overview = overview, // Все данные портфеля, рассчитанные в UseCase
                    availableMarketCoins = overview.marketCoins
                        .map { it.toCoinUiModel() }
                        .toImmutableList(),
                    assets = assets,
                    isBalanceHidden = securePrefs.isBalanceHidden
                )
            )
        )
    }
        .catch { e ->
            val secureErrorMessage = e.message?.let { UiText.DynamicString(it) }
                ?: UiText.StringResource(R.string.error_unknown)

            emit(
                PortfolioReducer.reduce(
                    oldState = PortfolioUiState.Loading,
                    action = PortfolioAction.ErrorOccurred(secureErrorMessage)
                )
            )
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PortfolioUiState.Loading
        )

    fun handleIntent(event: PortfolioEvent) {
        when (event) {
            is PortfolioEvent.AddTransaction -> {
                viewModelScope.launch(ioDispatcher) {
                    try {
                        portfolioUseCase.addTransaction(
                            coinId = event.coinId,
                            amount = event.amount,
                            purchasePrice = event.purchasePrice,
                            timestamp = System.currentTimeMillis()
                        )
                        _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_transaction_added)))
                    } catch (_: Exception) {
                        _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_transaction_add_error)))
                    }
                }
            }

            is PortfolioEvent.DeleteTransaction -> {
                viewModelScope.launch(ioDispatcher) {
                    try {
                        portfolioUseCase.deleteTransaction(event.transactionId)
                        _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_transaction_deleted)))
                    } catch (_: Exception) {
                        _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_transaction_delete_error)))
                    }
                }
            }

            is PortfolioEvent.AssetClicked -> {
                _effectChannel.trySend(PortfolioEffect.NavigateToGraph(event.coinId))
            }

            is PortfolioEvent.ClearPortfolio -> {
                viewModelScope.launch(ioDispatcher) {
                    try {
                        portfolioUseCase.clearPortfolio()
                        _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_portfolio_cleared)))
                    } catch (_: Exception) {
                        _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_portfolio_clear_error)))
                    }
                }
            }

            is PortfolioEvent.DeleteAsset -> {
                viewModelScope.launch(ioDispatcher) {
                    try {
                        portfolioUseCase.deleteAsset(event.coinId)
                        _effectChannel.trySend(
                            PortfolioEffect.ShowSnackbar(
                                UiText.StringResource(
                                    R.string.snackbar_asset_deleted,
                                    event.coinId.uppercase()
                                )
                            )
                        )
                    } catch (_: Exception) {
                        _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_asset_delete_error)))
                    }
                }
            }

            is PortfolioEvent.ToggleBalanceVisibility -> {
                viewModelScope.launch(ioDispatcher) {
                    try {
                        portfolioUseCase.toggleBalanceVisibility()
                    } catch (_: Exception) {
                        _effectChannel.trySend(
                            PortfolioEffect.ShowSnackbar(
                                UiText.StringResource(R.string.snackbar_error_visibility)
                            )
                        )
                    }
                }
            }
        }
    }
}