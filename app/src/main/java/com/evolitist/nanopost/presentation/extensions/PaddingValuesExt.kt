package com.evolitist.nanopost.presentation.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun PaddingValues.copy(
    layoutDirection: LayoutDirection,
    start: Dp = calculateStartPadding(layoutDirection),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(layoutDirection),
    bottom: Dp = calculateBottomPadding(),
) = PaddingValues(start, top, end, bottom)

@Composable
@ReadOnlyComposable
fun PaddingValues.copy(
    start: Dp = calculateStartPadding(LocalLayoutDirection.current),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(LocalLayoutDirection.current),
    bottom: Dp = calculateBottomPadding(),
) = PaddingValues(start, top, end, bottom)

@Composable
@ReadOnlyComposable
operator fun PaddingValues.plus(
    other: PaddingValues,
): PaddingValues {

    val ld = LocalLayoutDirection.current

    return PaddingValues(
        start = calculateStartPadding(ld) + other.calculateStartPadding(ld),
        top = calculateTopPadding() + other.calculateTopPadding(),
        end = calculateEndPadding(ld) + other.calculateEndPadding(ld),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
    )
}
