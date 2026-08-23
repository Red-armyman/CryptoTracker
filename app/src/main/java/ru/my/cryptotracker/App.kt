package ru.my.cryptotracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Включаем системный дебаг корутин, чтобы рантайм добавлял CoroutineName к имени потока
        System.setProperty("kotlinx.coroutines.debug", "on")

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Добавляем в начало каждого сообщения имя текущего потока и корутины
                    val threadName = Thread.currentThread().name
                    val formattedMessage = "[$threadName] $message"
                    super.log(priority, tag, formattedMessage, t)
                }
            })
        }
    }
}