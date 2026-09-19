package ru.my.cryptotracker.feature.dashboard.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import timber.log.Timber

@Composable
fun LogCompositionBranch(branchName: String) {
    // Локальный счетчик, который живет в SlotTable данного узла
    val compositionCounter = remember { object { var count = 0 } }
    compositionCounter.count++

    // Данный лог выстрелит В СТРОГОСТИ тогда, когда узел зайдет на Фазу 1 (Composition)!
    Timber.tag("CryptoNav").d("🔄 [Composition] Фаза 1 ЗАПУЩЕНА для: '$branchName'. Круг №: ${compositionCounter.count}")
}