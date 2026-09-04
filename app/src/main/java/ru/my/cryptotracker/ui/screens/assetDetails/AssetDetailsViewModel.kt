package ru.my.cryptotracker.ui.screens.assetDetails


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.my.cryptotracker.R
import ru.my.cryptotracker.core.common.di.IODispatcher
import ru.my.cryptotracker.core.ui.text.UiText
import ru.my.cryptotracker.ui.navgraphs.MainScreens
import ru.my.cryptotracker.ui.screens.portfolio.PortfolioEffect
import ru.my.cryptotracker.usecase.assetDetails.GetTransactionHistoryUseCase
import ru.my.cryptotracker.usecase.portfolio.DeleteTransactionUseCase
import javax.inject.Inject

@HiltViewModel
class AssetDetailsViewModel @Inject constructor(
    getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    savedStateHandle: SavedStateHandle,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val coinId: String = savedStateHandle.toRoute<MainScreens.AssetDetails>().coinId

    private val _effectChannel = Channel<PortfolioEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effectFlow = _effectChannel.receiveAsFlow()

    val uiState: StateFlow<AssetDetailsUiState> = getTransactionHistoryUseCase(coinId)
        .map { transactionsList ->
            if (transactionsList.isEmpty()) {
                AssetDetailsUiState.Empty
            } else {
                AssetDetailsUiState.Success(
                    coinId = coinId,
                    transactions = transactionsList.toImmutableList()
                )
            }
        }
        .catch { _ ->
            emit(AssetDetailsUiState.Error)
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AssetDetailsUiState.Loading
        )

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch(ioDispatcher) {
            try {
                deleteTransactionUseCase(transactionId)
                _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_transaction_deleted)))
            } catch (_: Exception) {
                _effectChannel.trySend(PortfolioEffect.ShowSnackbar(UiText.StringResource(R.string.snackbar_transaction_delete_error)))
            }
        }
    }
}