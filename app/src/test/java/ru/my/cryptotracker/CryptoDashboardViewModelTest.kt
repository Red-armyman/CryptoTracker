package ru.my.cryptotracker

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.my.cryptotracker.model.network.NetworkResult
import ru.my.cryptotracker.model.entities.CoinUiModel
import ru.my.cryptotracker.model.repository.CryptoDashboardRepository
import ru.my.cryptotracker.ui.screens.cryptoDashboard.CryptoDashboardUiState
import ru.my.cryptotracker.ui.screens.cryptoDashboard.CryptoDashboardViewModel
import ru.my.cryptotracker.ui.screens.cryptoDashboard.CryptoDashboardEvent
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class CryptoDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val repository: CryptoDashboardRepository = mockk()

    private lateinit var viewModel: CryptoDashboardViewModel

    private val fakeCachedCoins = listOf(
        CoinUiModel(id = "bitcoin", displayTicker = "BTC", displayPrice = "$65000.00"),
        CoinUiModel(id = "ethereum", displayTicker = "ETH", displayPrice = "$3400.00")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { repository.listenLocalCoinsCache() } returns flowOf(NetworkResult.Success(fakeCachedCoins))
        every { repository.isDataStaleFlow } returns MutableStateFlow(false)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `при вводе текста в поиск стейт должен отфильтроваться строго ПОСЛЕ истечения 300 миллисекунд дебаунса`() = runTest(testDispatcher) {

        viewModel = CryptoDashboardViewModel(
            repository = repository,
            ioDispatcher = testDispatcher
        )

        viewModel.coinsState.test {
            assertEquals(CryptoDashboardUiState.Loading, awaitItem())

            viewModel.handleIntent(CryptoDashboardEvent.SearchQueryChanged(query = "ETH"))

            testScheduler.advanceTimeBy(200.milliseconds)

            testScheduler.advanceTimeBy(100.milliseconds)

            val successItem = awaitItem()
            assert(successItem is CryptoDashboardUiState.Success)

            val filteredList = (successItem as CryptoDashboardUiState.Success).coinsList
            assertEquals(1, filteredList.size)
            assertEquals("ETH", filteredList.first().displayTicker)

            ensureAllEventsConsumed()
        }
    }
}