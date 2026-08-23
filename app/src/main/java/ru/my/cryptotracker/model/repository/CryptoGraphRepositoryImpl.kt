package ru.my.cryptotracker.model.repository

import ru.my.cryptotracker.model.network.CryptoApiService
import ru.my.cryptotracker.model.entities.GraphPoint
import ru.my.cryptotracker.model.mappers.toGraphPointsList
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoGraphRepositoryImpl @Inject constructor(
    private val apiService: CryptoApiService
) : CryptoGraphRepository {

    override suspend fun loadCoinHistoryUseCase(coinId: String): List<GraphPoint> {
        Timber.tag("CryptoNav").d("GraphRepository: Запрос истории котировок из сети для: $coinId")
        val historyDto = apiService.fetchCoinHistory(coinId = coinId)
        return historyDto.prices.toGraphPointsList()
    }
}