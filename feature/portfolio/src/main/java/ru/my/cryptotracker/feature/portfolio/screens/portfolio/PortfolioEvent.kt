package ru.my.cryptotracker.feature.portfolio.screens.portfolio

sealed interface PortfolioEvent {

    // Юзер открыл BottomSheet купли/продажи и вбил новую сделку
    data class AddTransaction(
        val coinId: String,
        val amount: Double,
        val purchasePrice: Double
    ) : PortfolioEvent

    // Юзер свайпнул сделку или нажал «Удалить» в истории транзакций
    data class DeleteTransaction(val transactionId: Long) : PortfolioEvent

    // Юзер нажал на монету в списке активов для перехода к детальной истории
    data class AssetClicked(val coinId: String) : PortfolioEvent

    // Юзер нажал кнопку полного сброса кошелька в ноль
    object ClearPortfolio : PortfolioEvent

    data class DeleteAsset(val coinId: String) : PortfolioEvent

    object ToggleBalanceVisibility : PortfolioEvent
}