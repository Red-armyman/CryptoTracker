package ru.my.cryptotracker.core.data.di

import android.content.Context
import androidx.room3.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.my.cryptotracker.core.data.database.CryptoDatabase
import ru.my.cryptotracker.core.data.database.dao.CoinDao
import ru.my.cryptotracker.core.data.database.dao.PortfolioDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCryptoDatabase(
        @ApplicationContext context: Context,
    ): CryptoDatabase {
        return Room.databaseBuilder(
            context,
            CryptoDatabase::class.java,
            "crypto_database.db"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinDao(database: CryptoDatabase): CoinDao {
        return database.coinDao
    }

    @Provides
    @Singleton
    fun providePortfolioDao(database: CryptoDatabase): PortfolioDao {
        return database.portfolioDao
    }
}