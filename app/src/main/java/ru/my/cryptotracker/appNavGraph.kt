package ru.my.cryptotracker

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ru.my.cryptotracker.core.common.navigation.MainScreens
import ru.my.cryptotracker.feature.dashboard.ui.CryptoDashboardScreen
import ru.my.cryptotracker.feature.portfolio.screens.assetDetails.AssetDetailsScreen
import ru.my.cryptotracker.feature.portfolio.screens.cryptoGraph.CryptoGraphScreen
import ru.my.cryptotracker.feature.portfolio.screens.portfolio.PortfolioScreen


fun NavGraphBuilder.appNavGraph(
    navController: NavHostController,
) {
    composable<MainScreens.Dashboard> {
        CryptoDashboardScreen(
            onNavigateToGraph = { coinId ->
                navController.navigate(
                    MainScreens.CryptoGraph(coinId = coinId)
                )
            }
        )
    }

    composable<MainScreens.Portfolio> {
        PortfolioScreen(
            onNavigateToGraph = { coinId ->
                navController.navigate(
                    MainScreens.CryptoGraph(coinId = coinId)
                )
            },
            onNavigateToTransactionHistory = { coinId ->
                navController.navigate(
                    MainScreens.AssetDetails(coinId = coinId)
                )
            }
        )
    }

    composable<MainScreens.CryptoGraph> {
        CryptoGraphScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<MainScreens.AssetDetails> {
        AssetDetailsScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}