package ru.my.cryptotracker.core.domain.security

import kotlinx.coroutines.flow.Flow
import ru.my.cryptotracker.core.model.SecurePreferences

interface SecurePreferencesRepository {

    val securePreferences: Flow<SecurePreferences>

    suspend fun saveSecureBalance(balance: Double)

    suspend fun setBalanceVisibility(isHidden: Boolean)

    suspend fun clearSecureStorage()
}