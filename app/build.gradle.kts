plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room3)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "ru.my.cryptotracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "ru.my.cryptotracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }


    testOptions {
        unitTests {
            all {
                it.maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
                it.forkEvery = 100
            }
        }
    }
}

protobuf {
    protoc {
        val version = libs.versions.protobuf.asProvider().get()
        artifact = "com.google.protobuf:protoc:$version"
    }
    generateProtoTasks {
        all().forEach { task ->
            // Включаем поддержку кэширования сборки для Protobuf
            task.outputs.cacheIf { true }

            task.builtins {
                create("java") {
                    option("lite")
                    outputSubDir = ""
                }
            }
        }
    }
}

dependencies {
    // Базовые Android зависимости
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
    implementation(libs.kotlinx.collections.immutable)

    // Твои бандлы из TOML (без дубликатов в коде)
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.network)
    implementation(libs.bundles.coil)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room 3
    implementation(libs.room3)
    ksp(libs.room3.compiler)

    // DataStore и безопасность (Строго через точки!)
    implementation(libs.androidx.datastore.core)
    implementation(libs.protobuf)
    implementation(libs.google.crypto.tink)

    // Инструменты отладки
    debugImplementation(libs.androidx.compose.ui.tooling)
    
    testImplementation(libs.bundles.unit.tests)

    implementation(libs.androidx.core.splashscreen)
}