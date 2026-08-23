package ru.my.cryptotracker.model.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.my.cryptotracker.model.network.dto.CoinDto
import ru.my.cryptotracker.model.network.dto.CoinHistoryDto

interface CryptoApiService {
    @GET("/api/v3/coins/markets")
    suspend fun fetchTopCoins(
        @Query("vs_currency") currency: String = "usd"
    ): List<CoinDto>

    @GET("/api/v3/coins/{id}/market_chart")
    suspend fun fetchCoinHistory(
        @Path("id") coinId: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: String = "7" // График за последние 7 дней
    ): CoinHistoryDto
}