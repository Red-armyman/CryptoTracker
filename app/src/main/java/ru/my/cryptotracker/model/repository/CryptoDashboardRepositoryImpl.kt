package ru.my.cryptotracker.model.repository

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.my.cryptotracker.model.network.CryptoApiService
import ru.my.cryptotracker.model.network.NetworkResult
import ru.my.cryptotracker.model.database.CryptoDatabase
import ru.my.cryptotracker.model.entities.CoinUiModel
import ru.my.cryptotracker.model.mappers.toDbEntitiesList
import ru.my.cryptotracker.model.mappers.toUiModelsList
import ru.my.cryptotracker.extensions.retryWithBackoff
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class CryptoDashboardRepositoryImpl @Inject constructor(
    private val apiService: CryptoApiService,
    private val db: CryptoDatabase,
) : CryptoDashboardRepository {

    private val _isDataStale = MutableStateFlow(false)
    override val isDataStaleFlow: StateFlow<Boolean> = _isDataStale.asStateFlow()

    override fun listenLocalCoinsCache(): Flow<NetworkResult<List<CoinUiModel>>> {
        return db.coinDao.listenAllCachedCoins()
            .map { entitiesList ->
                NetworkResult.Success(entitiesList.toUiModelsList())
            }
    }

    override suspend fun refreshCoinsNetworkCache() {
        while (true) {
            //дополнительно защищаюсь от ситуации, когда цикл снова начинает следующую итерацию после предыдущей операции.
            currentCoroutineContext().ensureActive()
            try {
                flow {
                    Timber.tag("CryptoNav").d("Репозиторий: Скачиваем свежие DTO из сети...")
                    val networkDtoList = apiService.fetchTopCoins()

                    if (networkDtoList.isEmpty()) {
                        // Сервер вернул [], базу не трогаем, бросаем ошибку для триггера ретраев!
                        Timber.tag("CryptoNav")
                            .w("Репозиторий: Сеть вернула пустой массив! Игнорируем зачистку SQLite.")
                        throw IOException("CoinGecko прислал пустой массив монет")
                    }

                    emit(networkDtoList)
                }
                    .retryWithBackoff(maxAttempts = 3, baseDelayMs = 10000L)
                    .onEach { networkDtoList ->
                        val entities = networkDtoList.toDbEntitiesList()
                        // Достаем текущий кэш из базы
                        val currentCache = db.coinDao.getCurrentCachedCoinsOnce()

                        if (entities.toSet() != currentCache.toSet()) {
                            // Данные на бирже изменились -> пишем в базу
                            db.coinDao.updateCoinsCache(entities)
                            Timber.tag("CryptoNav")
                                .d("Репозиторий: Данные изменились. SQLite кэш успешно ОБНОВЛЕН.")
                        } else {
                            // Данные идентичны -> игнорируем запись
                            Timber.tag("CryptoNav")
                                .d("Репозиторий: Котировки в сети идентичны кэшу. Пропускаем запись в SQLite.")
                        }
                        _isDataStale.value = false
                    }
                    .collect()

                // Если круг прошел успешно — планово спим 10 секунд перед новой сессией пуллинга
                delay(10000.milliseconds)

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _isDataStale.value = true
                // Сюда падаем, если сеть полностью мертва ИЛИ сервер выдает пустые [] даже после 3 ретраев
                Timber.tag("CryptoNav")
                    .e("❌ Сетевой воркер полностью исчерпал лимиты повторов retryWithBackoff. Сон 10 секунд перед новым кругом.")
                delay(10000.milliseconds)
            }
        }
    }
}