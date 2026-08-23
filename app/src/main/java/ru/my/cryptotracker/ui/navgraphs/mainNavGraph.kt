package ru.my.cryptotracker.ui.navgraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
) {
    coinsGraph(
        navController
    )
}