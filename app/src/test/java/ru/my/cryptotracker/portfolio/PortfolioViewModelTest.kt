package ru.my.cryptotracker.portfolio

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.my.cryptotracker.core.model.PortfolioOverview
import ru.my.cryptotracker.model.entities.CoinUiModel
import ru.my.cryptotracker.model.entities.PortfolioAssetUiModel
import ru.my.cryptotracker.core.data.security.EncryptedPreferencesManager
import ru.my.cryptotracker.prefs.proto.PortfolioPreferences
import ru.my.cryptotracker.ui.screens.portfolio.PortfolioUiState
import ru.my.cryptotracker.ui.screens.portfolio.PortfolioViewModel
import ru.my.cryptotracker.usecase.portfolio.GetPortfolioOverviewUseCase
import ru.my.cryptotracker.usecase.portfolio.PortfolioUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private val getPortfolioOverviewUseCase: GetPortfolioOverviewUseCase = mockk()
    private val securityManager: EncryptedPreferencesManager = mockk()
    private val portfolioUseCase: PortfolioUseCase = mockk()

    private lateinit var viewModel: PortfolioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `при успешном получении данных из UseCase вьюмодель должна выплюнуть состояние Success наружу`() =
        runTest(testDispatcher) {

            val fakeDomainOverview = PortfolioOverview(
                assets = emptyList(),
                marketCoins = emptyList(),
                totalBalanceUsd = 1500.0,
                totalProfitLossUsd = 250.0,
                totalProfitLossPercent = 20.0,
                isOffline = false
            )

            every {
                getPortfolioOverviewUseCase.invoke()
            } returns flowOf(fakeDomainOverview)

            every {
                securityManager.securePreferences
            } returns flowOf(
                PortfolioPreferences.getDefaultInstance()
            )

            viewModel = PortfolioViewModel(
                getPortfolioOverviewUseCase = getPortfolioOverviewUseCase,
                portfolioUseCase = portfolioUseCase,
                securityManager = securityManager,
                ioDispatcher = testDispatcher
            )

            viewModel.uiState.test {
                assertEquals(PortfolioUiState.Loading, awaitItem())

                testScheduler.advanceUntilIdle()

                val finalItem = awaitItem()
                assertTrue(finalItem is PortfolioUiState.Success)

                val successState = finalItem as PortfolioUiState.Success

                assertEquals(fakeDomainOverview, successState.overview)
                assertEquals(false, successState.isBalanceHidden)

                assertEquals(
                    persistentListOf<CoinUiModel>(),
                    successState.availableMarketCoins
                )

                assertEquals(
                    persistentListOf<PortfolioAssetUiModel>(),
                    successState.assets
                )

                ensureAllEventsConsumed()
            }
        }
}