plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.my.cryptotracker.core.ui"
    compileSdk {
        version = release(37)
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

    implementation(libs.bundles.compose.ui)
    testImplementation(libs.junit)
}