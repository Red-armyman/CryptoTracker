package ru.my.cryptotracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.my.cryptotracker.core.domain.repository.CryptoDashboardRepository
import ru.my.cryptotracker.core.domain.repository.CryptoGraphRepository
import ru.my.cryptotracker.core.domain.repository.CryptoMarketRepository
import ru.my.cryptotracker.core.domain.repository.CryptoPortfolioRepository
import ru.my.cryptotracker.core.data.repository.CryptoDashboardRepositoryImpl
import ru.my.cryptotracker.core.data.repository.CryptoGraphRepositoryImpl
import ru.my.cryptotracker.core.data.repository.CryptoMarketRepositoryImpl
import ru.my.cryptotracker.core.data.repository.CryptoPortfolioRepositoryImpl
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
