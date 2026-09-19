plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.my.cryptotracker"
    compileSdk = 37

    defaultConfig {
        applicationId = "ru.my.cryptotracker"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
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


dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:portfolio"))

    // Базовые Android зависимости
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
    implementation(libs.bundles.compose.ui)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    
    testImplementation(libs.bundles.unit.tests)

    implementation(libs.androidx.core.splashscreen)
}