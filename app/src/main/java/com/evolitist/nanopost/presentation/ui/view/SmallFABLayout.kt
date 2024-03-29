package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> SmallFABLayout(
    items: ImmutableList<T>,
    content: @Composable () -> Unit,
    label: @Composable (T) -> Unit,
    button: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutExpanded = remember { MutableTransitionState(false) }
    val transition = updateTransition(transitionState = layoutExpanded, label = "FAB layout")

    val fabElevation by transition.animateDp(
        label = "Main FAB elevation",
        targetValueByState = { if (it) 6.dp else 0.dp },
    )
    val fabContainerColor by transition.animateColor(
        label = "Main FAB container color",
        targetValueByState = { MaterialTheme.colorScheme.run { if (it) surface else primaryContainer } },
    )
    val fabContentColor by transition.animateColor(
        label = "Main FAB content color",
        targetValueByState = { MaterialTheme.colorScheme.run { if (it) onSurface else onPrimaryContainer } },
    )
    val iconRotation by transition.animateFloat(
        label = "Main FAB icon rotation",
        transitionSpec = { tween(easing = LinearOutSlowInEasing) },
        targetValueByState = { if (it) 135f else 0f },
    )
    val scrimAlpha by transition.animateFloat(
        label = "Scrim alpha",
        transitionSpec = { tween(easing = FastOutSlowInEasing) },
        targetValueByState = { if (it) .9f else 0f },
    )
    Box(
        modifier = modifier.padding(
            WindowInsets.safeContent.only(WindowInsetsSides.Bottom).asPaddingValues()
        ),
    ) {
        val showOverlay by remember {
            derivedStateOf { layoutExpanded.currentState || layoutExpanded.targetState }
        }

        ExpandingFAB(
            transition = transition,
            fabContainerColor = fabContainerColor,
            fabContentColor = fabContentColor,
            iconRotation = iconRotation,
            elevation = 6.dp - fabElevation,
            onClick = { layoutExpanded.targetState = true },
            content = content,
        )

        if (showOverlay) {
            Popup {
                val scrimColor = MaterialTheme.colorScheme.surface
                val systemUiController = rememberSystemUiController()

                DisposableEffect(systemUiController, scrimColor, scrimAlpha) {
                    systemUiController.setSystemBarsColor(scrimColor.copy(alpha = scrimAlpha))

                    onDispose {
                    }
                }

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(scrimColor.copy(alpha = scrimAlpha))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { layoutExpanded.targetState = false },
                )
            }
            Popup(
                alignment = Alignment.BottomEnd,
                onDismissRequest = { layoutExpanded.targetState = false },
                properties = PopupProperties(focusable = true),
            ) {
                SmallFABLayoutPopupContents(
                    transition = transition,
                    items = items,
                    label = label,
                    button = button,
                    expandingFab = {
                        ExpandingFAB(
                            transition = transition,
                            fabContainerColor = fabContainerColor,
                            fabContentColor = fabContentColor,
                            iconRotation = iconRotation,
                            elevation = fabElevation,
                            onClick = { layoutExpanded.targetState = false },
                            content = content,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun <T> SmallFABLayoutPopupContents(
    transition: Transition<Boolean>,
    items: ImmutableList<T>,
    expandingFab: @Composable () -> Unit,
    label: @Composable (T) -> Unit,
    button: @Composable (T) -> Unit,
) {
    val itemList = remember(items) { items.asReversed() }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.End,
    ) {
        itemList.forEachIndexed { index, item ->
            val i = itemList.lastIndex - index
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                modifier = Modifier.padding(end = 4.dp),
            ) {
                ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                    transition.AnimatedVisibility(
                        visible = { it },
                        enter = labelEnterTransition(i),
                        exit = fadeOut(animationSpec = tween()),
                        content = { label(item) },
                    )
                }

                transition.AnimatedVisibility(
                    visible = { it },
                    enter = buttonEnterTransition(i),
                    exit = fadeOut(animationSpec = tween()),
                    content = { button(item) },
                )
            }
        }

        // TODO figure out better approach
        expandingFab()
        /*transition.AnimatedVisibility(
            visible = { it },
            enter = fadeIn(animationSpec = tween()),
            exit = fadeOut(animationSpec = tween()),
            content = { expandingFab() },
        )*/
    }
}

@Composable
private fun ExpandingFAB(
    transition: Transition<Boolean>,
    fabContainerColor: Color,
    fabContentColor: Color,
    iconRotation: Float,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    elevation: Dp = 6.dp,
) {
    CompositionLocalProvider(LocalRippleTheme provides EmptyRippleTheme) {
        FloatingActionButton(
            containerColor = fabContainerColor,
            contentColor = fabContentColor,
            elevation = FloatingActionButtonDefaults.elevation(
                elevation,
                elevation,
                elevation,
                elevation,
            ),
            onClick = onClick,
            content = {
                transition.Crossfade(
                    animationSpec = tween(durationMillis = 250),
                    modifier = Modifier.rotate(iconRotation),
                ) {
                    if (it) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier.rotate(45f),
                        )
                    } else {
                        content()
                    }
                }
            },
        )
    }
}

private fun labelEnterTransition(index: Int): EnterTransition {
    return fadeIn(
        tween(durationMillis = 150, delayMillis = 75 + 50 * index)
    ) + slideInVertically(
        tween(
            durationMillis = 150,
            delayMillis = 75 + 50 * index,
            easing = LinearOutSlowInEasing,
        )
    ) { it }
}

private fun buttonEnterTransition(index: Int): EnterTransition {
    return fadeIn(
        tween(durationMillis = 100, delayMillis = 50 * index)
    ) + scaleIn(
        tween(durationMillis = 200, delayMillis = 50 * index)
    ) + slideInVertically(
        tween(
            durationMillis = 250,
            delayMillis = 50 * index,
            easing = LinearOutSlowInEasing,
        )
    ) { it / 2 }
}

@Immutable
private object EmptyRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = MaterialTheme.colorScheme.surface

    @Composable
    override fun rippleAlpha() = RippleAlpha(0f, 0f, 0f, 0f)
}
