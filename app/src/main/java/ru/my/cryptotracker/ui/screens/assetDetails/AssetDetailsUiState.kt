package ru.my.cryptotracker.ui.screens.assetDetails

import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.model.entities.PortfolioTransaction

sealed interface AssetDetailsUiState {
    object Loading : AssetDetailsUiState
    object Empty : AssetDetailsUiState
    data class Success(
        val coinId: String,
        val transactions: ImmutableList<PortfolioTransaction>
    ) : AssetDetailsUiState
    object Error : AssetDetailsUiState
}