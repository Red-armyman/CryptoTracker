package ru.my.cryptotracker.ui.screens.cryptoDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import ru.my.cryptotracker.R
import ru.my.cryptotracker.core.domain.repository.CryptoDashboardRepository
import ru.my.cryptotracker.di.IODispatcher
import ru.my.cryptotracker.model.mappers.toCoinUiModel
import ru.my.cryptotracker.ui.screens.cryptoDashboard.reducer.CoinsReducer
import ru.my.cryptotracker.ui.util.UiText
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CryptoDashboardViewModel @Inject constructor(
    private val repository: CryptoDashboardRepository,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _effectChannel = Channel<CryptoDashboardEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effectFlow = _effectChannel.receiveAsFlow()

    suspend fun startRefreshCoins() {
        try {
            repository.refreshCoinsNetworkCache()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _effectChannel.trySend(
                CryptoDashboardEffect.ShowSnackbar(
                    UiText.StringResource(R.string.snackbar_coins_refresh_error)
                )
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val coinsState: StateFlow<CryptoDashboardUiState> =
        _searchQuery
            .debounce(300.milliseconds)
            .flatMapLatest { query ->
                repository.listenLocalCoinsCache()
                    .map { marketCoins ->
                        val filteredList = marketCoins
                            .filter { coin ->
                                query.isEmpty() ||
                                        coin.symbol.contains(query, ignoreCase = true) ||
                                        coin.id.contains(query, ignoreCase = true)
                            }
                            .map { it.toCoinUiModel() }
                            .toImmutableList()

                        CoinsReducer.reduce(
                            oldState = CryptoDashboardUiState.Success(
                                coinsList = filteredList,
                                isOffline = false
                            ),
                            action = CoinsAction.CacheUpdated(
                                cachedCoins = filteredList
                            )
                        )
                    }
                    .catch { error ->
                        emit(
                            CoinsReducer.reduce(
                                oldState = CryptoDashboardUiState.Loading,
                                action = CoinsAction.NetworkErrorOccurred(
                                    error.message.orEmpty()
                                )
                            )
                        )
                    }
                    .combine(repository.isDataStaleFlow) { state, isDataStale ->
                        when (state) {
                            is CryptoDashboardUiState.Success ->
                                state.copy(isOffline = isDataStale)

                            else -> state
                        }
                    }
            }
            .flowOn(ioDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CryptoDashboardUiState.Loading
            )

    fun handleIntent(intent: CryptoDashboardEvent) {
        when (intent) {
            is CryptoDashboardEvent.SearchQueryChanged -> {
                if (_searchQuery.value == intent.query) return
                _searchQuery.value = intent.query
            }

            is CryptoDashboardEvent.CoinClicked -> {
                _effectChannel.trySend(
                    CryptoDashboardEffect.NavigateToGraph(intent.coinId)
                )
            }
        }
    }
}