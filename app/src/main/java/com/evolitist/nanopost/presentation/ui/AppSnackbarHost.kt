package com.evolitist.nanopost.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AppSnackbarHost(
    content: @Composable () -> Unit,
) {
    val hostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHost provides hostState,
    ) {
        Box {
            content()

            val contentWindowInsets = WindowInsets.systemBars
            SnackbarHost(
                hostState = hostState,
                modifier = Modifier
                    .windowInsetsPadding(
                        contentWindowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                    )
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            )
        }
    }
}

val LocalSnackbarHost = staticCompositionLocalOf<SnackbarHostState?> { null }
