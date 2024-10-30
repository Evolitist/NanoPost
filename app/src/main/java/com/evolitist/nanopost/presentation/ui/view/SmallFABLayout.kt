package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.evolitist.nanopost.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun <T> SmallFABLayout(
    items: ImmutableList<T>,
    label: @Composable (T) -> Unit,
    button: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    contentInsets: WindowInsets = WindowInsets.systemBars,
) {
    val layoutExpanded = remember { MutableTransitionState(false) }
    val transition = rememberTransition(transitionState = layoutExpanded, label = "FAB layout")

    val scrimColor = MaterialTheme.colorScheme.background
    val scrimAlpha by transition.animateFloat(
        label = "Scrim alpha",
        transitionSpec = { tween(easing = FastOutSlowInEasing) },
        targetValueByState = { if (it) .9f else 0f },
    )
    val showOverlay by remember {
        derivedStateOf { layoutExpanded.currentState || layoutExpanded.targetState }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .fillMaxSize()
            .zIndex(100f)
            /*.clickable(
                interactionSource = null,
                indication = null,
                enabled = showOverlay,
                onClick = { layoutExpanded.targetState = false },
            )*/
            .drawBehind { drawRect(scrimColor, alpha = scrimAlpha) }
            .windowInsetsPadding(contentInsets)
            .padding(16.dp),
    ) {
        if (showOverlay) {
            SmallFABLayoutPopupContents(
                transition = transition,
                items = items,
                label = label,
                button = button,
            )
        }

        ExpandingFAB(
            transition = transition,
            onClick = { layoutExpanded.targetState = !layoutExpanded.targetState },
        )
    }
}

@Composable
private fun ExpandingFAB(
    transition: Transition<Boolean>,
    onClick: () -> Unit,
) {
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

    FloatingActionButton(
        containerColor = fabContainerColor,
        contentColor = fabContentColor,
        onClick = onClick,
        content = {
            transition.Crossfade(
                animationSpec = tween(durationMillis = 250),
                modifier = Modifier.graphicsLayer { rotationZ = iconRotation },
            ) {
                if (it) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier.rotate(45f),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@Composable
private fun <T> ColumnScope.SmallFABLayoutPopupContents(
    transition: Transition<Boolean>,
    items: ImmutableList<T>,
    label: @Composable (T) -> Unit,
    button: @Composable (T) -> Unit,
) {
    for (i in items.indices.reversed()) {
        val item = items.get(i)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
            modifier = Modifier.padding(end = 4.dp),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
            ) {
                ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                    transition.AnimatedVisibility(
                        visible = { it },
                        enter = labelEnterTransition(i),
                        exit = fadeOut(animationSpec = tween()),
                        content = { label(item) },
                    )
                }
            }

            transition.AnimatedVisibility(
                visible = { it },
                enter = buttonEnterTransition(i),
                exit = fadeOut(animationSpec = tween()),
                content = { button(item) },
            )
        }
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

@Preview
@Composable
private fun SmallFABLayout2_Preview() {
    SmallFABLayout(
        items = persistentListOf(0, 1),
        label = {
            when (it) {
                0 -> Text(stringResource(R.string.post))
                1 -> Text(stringResource(R.string.profile))
            }
        },
        button = {
            when (it) {
                0 -> SmallFloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.PostAdd,
                            contentDescription = stringResource(R.string.post),
                        )
                    },
                )

                1 -> SmallFloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.GroupAdd,
                            contentDescription = stringResource(R.string.profile),
                        )
                    },
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    )
}
