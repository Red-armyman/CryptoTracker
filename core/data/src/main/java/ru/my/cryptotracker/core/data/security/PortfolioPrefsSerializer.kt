package ru.my.cryptotracker.core.data.security

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.crypto.tink.Aead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.my.cryptotracker.prefs.proto.PortfolioPreferences
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Шифрует весь бинарный файл Protobuf-схемы целиком по алгоритму AES-256!
 */
class PortfolioPrefsSerializer @Inject constructor(
    private val aead: Aead // Внедряем крипто-движок Google Tink
) : Serializer<PortfolioPreferences> {

    // Исходное дефолтное состояние схемы (если файла еще нет на диске)
    override val defaultValue: PortfolioPreferences
        get() = PortfolioPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PortfolioPreferences {
        return withContext(Dispatchers.IO) {
            try {
                val encryptedBytes = input.readBytes()
                if (encryptedBytes.isEmpty()) {
                    return@withContext defaultValue
                }

                // Дешифруем бинарный дамп через примитивы Google Tink
                val decryptedBytes = aead.decrypt(encryptedBytes, null)
                PortfolioPreferences.parseFrom(decryptedBytes)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.tag("SecurityStorage")
                    .e(e, "❌ Ошибка чтения или дешифрования Protobuf-файла с диска")

                // Оборачиваем сбой в CorruptionException, чтобы DataStore
                // запустил нативный обработчик восстановления данных (CorruptionHandler)
                throw CorruptionException("Критические данные повреждены", e)
            }
        }
    }

    override suspend fun writeTo(t: PortfolioPreferences, output: OutputStream) {
        withContext(Dispatchers.IO) {
            try {
                val rawBytes = t.toByteArray()
                val encryptedBytes = aead.encrypt(rawBytes, null)

                output.write(encryptedBytes)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Timber.tag("SecurityStorage")
                    .e(e, "❌ Критическая ошибка ввода-вывода (I/O) при записи Protobuf на диск")

                // Пробрасываем IOException наружу, сообщая DataStore о сбое для отката транзакции
                throw e
            } catch (e: Exception) {
                Timber.tag("SecurityStorage")
                    .e(e, "❌ Непредвиденный сбой крипто-движка Keystore при шифровании")

                throw IOException("Сбой аппаратного шифрования", e)
            }
        }
    }
}