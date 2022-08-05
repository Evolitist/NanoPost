package com.evolitist.nanopost.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.evolitist.nanopost.presentation.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.showAuthFlow.value == null
            }
        }

        setContent {
            CompositionLocalProvider(ProvidableLocalActivityViewModel provides viewModel) {
                AppTheme {
                    AppNavGraph()
                }
            }
        }
    }
}

@Suppress("CompositionLocalNaming")
private val ProvidableLocalActivityViewModel = staticCompositionLocalOf<MainViewModel> {
    error("Activity ViewModel was not provided in this scope")
}
val LocalActivityViewModel: CompositionLocal<MainViewModel> = ProvidableLocalActivityViewModel
