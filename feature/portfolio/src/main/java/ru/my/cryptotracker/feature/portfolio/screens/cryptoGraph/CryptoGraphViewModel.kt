package ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import ru.my.cryptotracker.core.common.di.IODispatcher
import ru.my.cryptotracker.core.common.navigation.MainScreens
import ru.my.cryptotracker.core.domain.usecase.cryptoGraph.LoadCoinHistoryUseCase
import ru.my.cryptotracker.core.network.extensions.retryWithBackoff
import ru.my.cryptotracker.feature.portfolio.mappers.toGraphPointUiModelList
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class CryptoGraphViewModel @Inject constructor(
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val loadCoinHistoryUseCase: LoadCoinHistoryUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val routeArgs = savedStateHandle.toRoute<MainScreens.CryptoGraph>()
    val coinId: String = routeArgs.coinId

    private val retryTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CryptoGraphUiState> = retryTrigger
        .flatMapLatest {
            flow {
                emit(CryptoGraphUiState(coinId = coinId, graphState = GraphUiState.Loading()))

                Timber.tag("CryptoNav").d("GraphViewModel: Запрос в сеть для монеты: $coinId")
                val domainPoints = loadCoinHistoryUseCase(coinId = coinId)
                val uiPoints = domainPoints.toGraphPointUiModelList().toImmutableList()

                emit(CryptoGraphUiState(coinId = coinId, graphState = GraphUiState.Success(points = uiPoints)))
            }
                .retryWithBackoff(maxAttempts = 3, baseDelayMs = 10000L)
                .catch { e ->
                    if (e is CancellationException) throw e

                    Timber.tag("CryptoNav").e("GraphViewModel: Сеть полностью легла на графике. Выводим ошибку.")

                    emit(
                        CryptoGraphUiState(
                            coinId = coinId,
                            graphState = GraphUiState.Error(message = "Не удалось загрузить график. Проверьте интернет.")
                        )
                    )
                }
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CryptoGraphUiState(coinId = coinId, graphState = GraphUiState.Loading())
        )

    fun retryLoading() {
        retryTrigger.tryEmit(Unit)
    }
}