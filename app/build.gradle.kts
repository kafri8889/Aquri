plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.anafthdev.aquri"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.anafthdev.aquri"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
//
//    kotlinOptions {
//        jvmTarget = "11"
//    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // --- Default ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- Navigation ---
    implementation(libs.androidx.navigation.compose)

    // --- Lifecycle & ViewModel ---
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // --- Room ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- Hilt ---
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // --- WorkManager + Hilt ---
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // --- DataStore ---
    implementation(libs.androidx.datastore.preferences)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android)

    // --- Kotlinx Serialization ---
    implementation(libs.kotlinx.serialization.json)

    // --- Chart (Vico) ---
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    // --- Splash Screen ---
    implementation(libs.androidx.splashscreen)

    // --- Icons Extended ---
    implementation(libs.androidx.compose.material.icons.extended)

    // --- Firebase ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
