package ru.my.cryptotracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.my.cryptotracker.model.repository.CryptoDashboardRepository
import ru.my.cryptotracker.model.repository.CryptoDashboardRepositoryImpl
import ru.my.cryptotracker.model.repository.CryptoGraphRepository
import ru.my.cryptotracker.model.repository.CryptoGraphRepositoryImpl
import ru.my.cryptotracker.model.repository.CryptoMarketRepository
import ru.my.cryptotracker.model.repository.CryptoMarketRepositoryImpl
import ru.my.cryptotracker.model.repository.CryptoPortfolioRepository
import ru.my.cryptotracker.model.repository.CryptoPortfolioRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCryptoGraphRepository(
        impl: CryptoGraphRepositoryImpl
    ): CryptoGraphRepository

    @Binds
    @Singleton
    abstract fun bindCryptoDashboardRepository(
        impl: CryptoDashboardRepositoryImpl
    ): CryptoDashboardRepository

    @Binds
    @Singleton
    abstract fun bindCryptoPortfolioRepositoryImpl(
        impl: CryptoPortfolioRepositoryImpl
    ): CryptoPortfolioRepository

    @Binds
    @Singleton
    abstract fun bindCryptoMarketRepository(
        impl: CryptoMarketRepositoryImpl
    ): CryptoMarketRepository
}
