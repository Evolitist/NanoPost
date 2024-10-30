plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.gms)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    compileSdk = 35
    namespace = "com.evolitist.nanopost"

    defaultConfig {
        applicationId = "com.evolitist.nanopost"
        minSdk = 27
        targetSdk = 35
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
        isCoreLibraryDesugaringEnabled = true
    }

    lint {
        lintConfig = rootProject.file("quality/lint.xml")
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xcontext-receivers",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
        )
    }
}

dependencies {
    implementation(libs.androidx.profileinstaller)
    baselineProfile(project(mapOf("path" to ":baselineprofile")))

    coreLibraryDesugaring(libs.desugaring)
    implementation(libs.kotlin.collections)
    implementation(libs.kotlin.datetime)
    implementation(libs.kotlin.serialization)
    implementation(libs.bundles.kmmutils)

    // UI
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.appcompat.resources)
    implementation(libs.androidx.splashscreen)

    implementation(libs.compose.animation)
    implementation(libs.compose.blurhash)
    implementation(libs.compose.coil.base)
    implementation(libs.compose.coil.network)
    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material.motion)
    implementation(libs.compose.navigation)
    implementation(libs.compose.paging)
    implementation(libs.compose.tracing)
    implementation(libs.compose.ui)
    implementation(libs.compose.uiUtil)
    implementation(libs.compose.viewmodel)

    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.placeholder.material)

    // DI
    implementation(libs.hilt.android.runtime)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.compose)

    // Storage
    implementation(libs.androidx.datastore)

    // Network
    implementation(libs.okhttp)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.auth)
    implementation(libs.ktor.contentNegotiation)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.serialization)

    // Background tasks
    implementation(libs.androidx.work)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    // Debug stuff
    debugImplementation(libs.slf4j)
    debugImplementation(libs.compose.uiTooling.common)
    implementation(libs.compose.uiTooling.preview)
}
