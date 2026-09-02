package ru.my.cryptotracker.core.data.database

import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import ru.my.cryptotracker.core.data.database.dao.CoinDao
import ru.my.cryptotracker.core.data.database.dao.PortfolioDao
import ru.my.cryptotracker.core.model.database.entity.CoinDb
import ru.my.cryptotracker.core.model.database.entity.PortfolioDb

@Database(
    entities = [
        CoinDb::class,
        PortfolioDb::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class CryptoDatabase : RoomDatabase() {
    abstract val coinDao: CoinDao
    abstract val portfolioDao: PortfolioDao
}