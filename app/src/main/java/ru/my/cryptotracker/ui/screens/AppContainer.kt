package ru.my.cryptotracker.ui.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.my.cryptotracker.R
import ru.my.cryptotracker.ui.navgraphs.MainScreens
import ru.my.cryptotracker.ui.navgraphs.mainNavGraph

@Composable
fun AppContainer() {
    val tabItems = remember {
        listOf(
            NavigationTabItem(
                titleResId = R.string.nav_tab_market,
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                route = MainScreens.Dashboard
            ),
            NavigationTabItem(
                titleResId = R.string.nav_tab_portfolio,
                icon = Icons.Default.BusinessCenter,
                route = MainScreens.Portfolio
            )
        )
    }

    val navController = rememberNavController()
    val contentInsets = WindowInsets.systemBars
        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = tabItems.any { tab ->
        currentDestination?.hasRoute(tab.route::class) == true
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentWindowInsets = contentInsets,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    tabItems.forEach { tab ->

                        val isSelected =
                            currentDestination?.hasRoute(tab.route::class) == true

                        NavigationBarItem(
                            selected = isSelected,
                            label = { Text(stringResource(id = tab.titleResId)) },
                            icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(
                                        navController.graph.findStartDestination().id
                                    ) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = MainScreens.Dashboard
        ) {
            mainNavGraph(
                navController = navController,
            )
        }
    }
}

@Immutable
data class NavigationTabItem(
    val titleResId: Int,
    val icon: ImageVector,
    val route: MainScreens
)