package ru.my.cryptotracker.usecase.portfolio

import kotlinx.coroutines.flow.first
import ru.my.cryptotracker.model.security.EncryptedPreferencesManager
import javax.inject.Inject

class ToggleBalanceVisibilityUseCase @Inject constructor(
    private val encryptedPreferencesManager: EncryptedPreferencesManager
) {
    suspend operator fun invoke() {
        val currentHiddenState = encryptedPreferencesManager.securePreferences.first().isBalanceHidden
        encryptedPreferencesManager.setBalanceVisibility(!currentHiddenState)
    }
}