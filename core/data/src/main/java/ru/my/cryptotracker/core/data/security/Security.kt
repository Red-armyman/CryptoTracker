package ru.my.cryptotracker.core.data.security

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.my.cryptotracker.core.domain.security.SecurePreferencesRepository
import ru.my.cryptotracker.core.model.SecurePreferences
import ru.my.cryptotracker.prefs.proto.PortfolioPreferences
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPreferencesManager @Inject constructor(
    private val protoDataStore: DataStore<PortfolioPreferences>
) : SecurePreferencesRepository {

    override val securePreferences: Flow<SecurePreferences> =
        protoDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(PortfolioPreferences.getDefaultInstance())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                SecurePreferences(
                    totalBalanceUsd = prefs.totalBalanceUsd,
                    isBalanceHidden = prefs.isBalanceHidden,
                    activeCurrency = prefs.activeCurrency,
                    lastSyncTimestamp = prefs.lastSyncTimestamp
                )
            }

    override suspend fun saveSecureBalance(balance: Double) {
        protoDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setTotalBalanceUsd(balance)
                .build()
        }
    }

    override suspend fun setBalanceVisibility(isHidden: Boolean) {
        protoDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsBalanceHidden(isHidden)
                .build()
        }
    }

    override suspend fun clearSecureStorage() {
        protoDataStore.updateData {
            PortfolioPreferences.getDefaultInstance()
        }
    }
}