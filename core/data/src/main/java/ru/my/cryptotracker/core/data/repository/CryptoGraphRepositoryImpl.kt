package ru.my.cryptotracker.core.data.repository

import ru.my.cryptotracker.core.data.mappers.toGraphPointsList
import ru.my.cryptotracker.core.domain.repository.CryptoGraphRepository
import ru.my.cryptotracker.core.model.GraphPoint
import ru.my.cryptotracker.core.data.network.CryptoApiService
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