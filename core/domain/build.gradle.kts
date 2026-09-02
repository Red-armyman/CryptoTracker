plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.my.cryptotracker.core.domain"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
}