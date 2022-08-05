plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.gms)
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.evolitist.nanopost"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
            signingConfig = signingConfigs["debug"]
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.kotlin.collections)
    implementation(libs.kotlin.serialization)

    // UI
    implementation(libs.androidx.activity)
    implementation(libs.androidx.splashscreen)

    implementation(libs.compose.animation)
    implementation(libs.compose.coil)
    implementation(libs.compose.compiler)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.v3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material.motion)
    implementation(libs.compose.navigation)
    implementation(libs.compose.paging)
    implementation(libs.compose.ui)
    implementation(libs.compose.viewmodel)

    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.systemuicontroller)

    // DI
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.compose)

    // Storage
    implementation(libs.androidx.datastore)

    // Network
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.auth)
    implementation(libs.ktor.contentnegotiation)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.serialization)

    // Background tasks
    implementation(libs.androidx.work)
    implementation(libs.hilt.work)
    kapt(libs.hilt.work.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    // Debug stuff
    debugImplementation(libs.slf4j)
    debugImplementation(libs.compose.ui.tooling)
}
