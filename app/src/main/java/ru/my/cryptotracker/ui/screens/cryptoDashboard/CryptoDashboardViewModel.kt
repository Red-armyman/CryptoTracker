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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import ru.my.cryptotracker.R
import ru.my.cryptotracker.model.network.NetworkResult
import ru.my.cryptotracker.di.IODispatcher
import ru.my.cryptotracker.model.repository.CryptoDashboardRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import ru.my.cryptotracker.ui.util.UiText

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
                    .combine(repository.isDataStaleFlow) { result, isDataStale ->
                        when (result) {
                            is NetworkResult.Loading ->
                                CryptoDashboardUiState.Loading

                            is NetworkResult.Error ->
                                CoinsReducer.reduce(
                                    oldState = CryptoDashboardUiState.Loading,
                                    action = CoinsAction.NetworkErrorOccurred(result.message)
                                )

                            is NetworkResult.Success -> {
                                val filteredList = result.data
                                    .filter { coin ->
                                        query.isEmpty() ||
                                                coin.displayTicker.contains(
                                                    query,
                                                    ignoreCase = true
                                                ) ||
                                                coin.id.contains(
                                                    query,
                                                    ignoreCase = true
                                                )
                                    }
                                    .toImmutableList()

                                CoinsReducer.reduce(
                                    oldState = CryptoDashboardUiState.Success(
                                        coinsList = filteredList,
                                        isOffline = isDataStale
                                    ),
                                    action = CoinsAction.CacheUpdated(
                                        cachedCoins = filteredList
                                    )
                                )
                            }
                        }
                    }
                    .distinctUntilChanged()
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