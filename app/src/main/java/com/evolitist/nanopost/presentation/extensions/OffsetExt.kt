package com.evolitist.nanopost.presentation.extensions

import androidx.compose.ui.geometry.Offset

fun Offset.coerceIn(minimumValue: Offset, maximumValue: Offset): Offset {
    return Offset(
        x.coerceIn(minimumValue.x, maximumValue.x),
        y.coerceIn(minimumValue.y, maximumValue.y),
    )
}
