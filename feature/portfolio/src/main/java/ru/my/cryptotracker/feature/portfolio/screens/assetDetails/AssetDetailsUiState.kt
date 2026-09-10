package ru.my.cryptotracker.feature.portfolio.screens.assetDetails

import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.core.model.PortfolioTransaction

sealed interface AssetDetailsUiState {
    object Loading : AssetDetailsUiState
    object Empty : AssetDetailsUiState
    data class Success(
        val coinId: String,
        val transactions: ImmutableList<PortfolioTransaction>
    ) : AssetDetailsUiState
    object Error : AssetDetailsUiState
}