package com.evolitist.nanopost.presentation.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colors = remember(darkTheme, context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getDynamicColorScheme(context, darkTheme)
        } else {
            getColorScheme(darkTheme)
        }
    }
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme,
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}

fun getColorScheme(darkTheme: Boolean) = if (darkTheme) {
    darkColorScheme()
} else {
    lightColorScheme()
}

@RequiresApi(Build.VERSION_CODES.S)
fun getDynamicColorScheme(context: Context, darkTheme: Boolean) = if (darkTheme) {
    dynamicDarkColorScheme(context)
} else {
    dynamicLightColorScheme(context)
}
