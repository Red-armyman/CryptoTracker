plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.my.cryptotracker.core.model"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    testImplementation(libs.junit)
}