package ru.my.cryptotracker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.my.cryptotracker.BuildConfig
import ru.my.cryptotracker.model.network.CryptoApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // 1. Создаем и настраиваем перехватчик логов
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // 2. ВШИВАЕМ ЕГО В КЛИЕНТ
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideCryptoApiService(okHttpClient: OkHttpClient): CryptoApiService {
        val contentType = "application/json".toMediaType()
        val jsonConfig = Json { ignoreUnknownKeys = true } // Защита от лишних полей в JSON

        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com")
            .client(okHttpClient)
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .build()
            .create(CryptoApiService::class.java)
    }
}