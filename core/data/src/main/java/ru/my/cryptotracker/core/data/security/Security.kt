package ru.my.cryptotracker.core.data.security

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import ru.my.cryptotracker.prefs.proto.PortfolioPreferences
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPreferencesManager @Inject constructor(
    private val protoDataStore: DataStore<PortfolioPreferences>
) {

    val securePreferences: Flow<PortfolioPreferences> = protoDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(PortfolioPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun saveSecureBalance(balance: Double) {
        protoDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setTotalBalanceUsd(balance)
                .build()
        }
    }


    /**
     * Переключение флага видимости баланса звездочками (***)
     */
    suspend fun setBalanceVisibility(isHidden: Boolean) {
        protoDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsBalanceHidden(isHidden)
                .build()
        }
    }

    /**
     * Полный сброс
     */
    suspend fun clearSecureStorage() {
        protoDataStore.updateData {
            PortfolioPreferences.getDefaultInstance()
        }
    }
}