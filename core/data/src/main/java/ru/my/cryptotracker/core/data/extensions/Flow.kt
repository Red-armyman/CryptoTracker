package ru.my.cryptotracker.core.data.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Экспоненциальный повтор с джиттером
 * @param maxAttempts Максимальное количество попыток до того, как сдаться
 * @param baseDelayMs Начальная базовая задержка в миллисекундах
 */
fun <T> Flow<T>.retryWithBackoff(
    maxAttempts: Int = 4,
    baseDelayMs: Long = 1000L
): Flow<T> = retryWhen { cause, attempt ->
    if (cause is CancellationException) throw cause

    // Глобальный Селектор Ошибок: Повторяем ТОЛЬКО сетевые сбои (сеть или парсер)
    if (cause is IOException || cause is HttpException || cause is SerializationException) {
        if (attempt < maxAttempts) {
            // Расчет экспоненты: baseDelay * 2^attempt
            val factor = 2.0.pow(attempt.toDouble()).toLong()
            val calculatedDelay = baseDelayMs * factor

            // Внедряем Джиттер: добавляем случайный шум от -200мс до +500мс
            val jitter = Random.nextLong(-200L, 500L)
            val finalDelay = (calculatedDelay + jitter).coerceAtLeast(100L)

            Timber.tag("CryptoNav").w(
                "⚠️ Сбой сети (${cause.message}). Попытка повтора №${attempt + 1}. " +
                        "Математический делей: ${finalDelay}мс (Экспонента: ${calculatedDelay}мс, Джиттер: ${jitter}мс)"
            )

            delay(finalDelay.milliseconds)
            return@retryWhen true // Разрешаем корутине сделать повторный заход в поток!
        }
    }

    // Если ошибка неизвестная или лимит попыток исчерпан — сдаемся и пробрасываем краш дальше
    Timber.tag("CryptoNav").e("❌ Лимит попыток исчерпан или ошибка критическая. Сдаемся.")
    false
}