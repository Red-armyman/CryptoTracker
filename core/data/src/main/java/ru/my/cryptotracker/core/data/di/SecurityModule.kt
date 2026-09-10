package ru.my.cryptotracker.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.my.cryptotracker.core.data.security.EncryptedPreferencesManager
import ru.my.cryptotracker.core.data.security.PortfolioPrefsSerializer
import ru.my.cryptotracker.prefs.proto.PortfolioPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideAead(
        @ApplicationContext context: Context
    ): Aead {
        AeadConfig.register()
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, "tink_master_keyset", "tink_master_key_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://tink_master_key")
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    @Provides
    @Singleton
    fun providePortfolioPrefsSerializer(
        aead: Aead
    ): PortfolioPrefsSerializer {
        return PortfolioPrefsSerializer(aead)
    }

    @Provides
    @Singleton
    fun provideProtoDataStore(
        @ApplicationContext context: Context,
        serializer: PortfolioPrefsSerializer
    ): DataStore<PortfolioPreferences> {
        return DataStoreFactory.create(
            serializer = serializer,
            produceFile = { context.dataStoreFile("encrypted_portfolio_settings.pb") }
        )
    }

    @Provides
    @Singleton
    fun provideSecurityManager(
        protoDataStore: DataStore<PortfolioPreferences>
    ): EncryptedPreferencesManager {
        return EncryptedPreferencesManager(protoDataStore)
    }
}