package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
@NonRestartableComposable
fun ModalBottomSheetLayout(
    bottomSheetNavigator: BottomSheetNavigator,
    modifier: Modifier = Modifier,
    sheetShape: Shape = MaterialTheme.shapes.large.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize),
    sheetElevation: Dp = ModalBottomSheetElevation,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(ModalBottomSheetElevation),
    sheetContentColor: Color = MaterialTheme.colorScheme.onSurface,
    scrimColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f),
    content: @Composable () -> Unit,
) {
    com.google.accompanist.navigation.material.ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        modifier = modifier,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        scrimColor = scrimColor,
        content = content,
    )
}

private val ModalBottomSheetElevation = 6.dp
