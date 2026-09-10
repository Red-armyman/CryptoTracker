plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room3)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.my.cryptotracker.core.data"
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

    buildFeatures {
        buildConfig = true
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

protobuf {
    protoc {
        val version = libs.versions.protobuf.asProvider().get()
        artifact = "com.google.protobuf:protoc:$version"
    }

    generateProtoTasks {
        all().forEach { task ->
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
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))

   // Базовые Android зависимости
    implementation(libs.timber)

    implementation(libs.bundles.network)
    implementation(libs.androidx.annotation.experimental)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room 3
    implementation(libs.room3)
    ksp(libs.room3.compiler)

    // DataStore и безопасность
    implementation(libs.androidx.datastore.core)
    implementation(libs.protobuf)
    implementation(libs.google.crypto.tink)

    testImplementation(libs.junit)
}