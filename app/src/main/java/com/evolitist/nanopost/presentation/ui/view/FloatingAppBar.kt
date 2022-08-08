package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * ![Small top app bar image](https://developer.android.com/images/reference/androidx/compose/material3/small-top-app-bar.png)
 *
 * Material Design small top app bar.
 *
 * The top app bar displays information and actions relating to the current screen.
 *
 * This SmallTopAppBar has slots for a title, navigation icon, and actions.
 *
 * @param title the title to be displayed in the top app bar
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon The navigation icon displayed at the start of the top app bar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors a [TopAppBarColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [TopAppBarDefaults.smallTopAppBarColors].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 */
@Composable
fun FloatingAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    shape: Shape = CircleShape,
    colors: TopAppBarColors = TopAppBarDefaults.smallTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = WindowInsets.statusBars.only(WindowInsetsSides.Top).asPaddingValues(),
) {
    // Sets the app bar's height offset to collapse the entire bar's height when content is
    // scrolled.
    val heightOffsetLimit = with(LocalDensity.current) {
        -(FloatingAppBarHeight + FloatingAppBarTopMargin + contentPadding.calculateTopPadding()).toPx()
    }
    SideEffect {
        if (scrollBehavior?.state?.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior?.state?.heightOffsetLimit = heightOffsetLimit
        }
    }

    // Color transition fraction is intentionally set to 1.0 here as [FloatingAppBar] should
    // always appear raised
    val colorTransitionFraction = 1f

    // Wrap the given actions in a Row.
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }

    // Set up support for shifting the floating app bar when vertically dragging the bar itself.
    val appBarDragModifier = Modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState { delta ->
            if (scrollBehavior != null && !scrollBehavior.isPinned) {
                scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
            }
        }
    )

    // Compose a Surface with a FloatingAppBarLayout content. The offset of the app bar will be
    // determined by the current scroll-state offset.
    Surface(
        modifier = modifier
            .then(appBarDragModifier)
            .padding(contentPadding)
            .padding(horizontal = FloatingAppBarHorizontalMargin)
            .padding(top = FloatingAppBarTopMargin)
            .offset {
                IntOffset(
                    x = 0,
                    y = scrollBehavior?.state?.heightOffset?.roundToInt() ?: 0,
                )
            },
        shape = shape,
        color = colors.containerColor(colorTransitionFraction).value,
        shadowElevation = FloatingAppBarElevation,
    ) {
        FloatingAppBarLayout(
            modifier = Modifier,
            navigationIconContentColor = colors.navigationIconContentColor(colorTransitionFraction).value,
            titleContentColor = colors.titleContentColor(colorTransitionFraction).value,
            actionIconContentColor = colors.actionIconContentColor(colorTransitionFraction).value,
            title = title,
            titleTextStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            titleHorizontalArrangement = Arrangement.Center,
            hideTitleSemantics = false,
            navigationIcon = navigationIcon,
            actions = actionsRow,
        )
    }
}

/**
 * The base [Layout] for all top app bars. This function lays out a top app bar navigation icon
 * (leading icon), a title (header), and action icons (trailing icons). Note that the navigation and
 * the actions are optional.
 *
 * @param navigationIconContentColor the content color that will be applied via a
 * [LocalContentColor] when composing the navigation icon
 * @param titleContentColor the color that will be applied via a [LocalContentColor] when composing
 * the title
 * @param actionIconContentColor the content color that will be applied via a [LocalContentColor]
 * when composing the action icons
 * @param title the top app bar title (header)
 * @param titleTextStyle the title's text style
 * @param modifier a [Modifier]
 * @param titleHorizontalArrangement the title's horizontal arrangement
 * @param hideTitleSemantics hides the title node from the semantic tree. Apply this
 * boolean when this layout is part of a [TwoRowsTopAppBar] to hide the title's semantics
 * from accessibility services. This is needed to avoid having multiple titles visible to
 * accessibility services at the same time, when animating between collapsed / expanded states.
 * @param navigationIcon a navigation icon [Composable]
 * @param actions actions [Composable]
 */
@Composable
private fun FloatingAppBarLayout(
    modifier: Modifier,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleHorizontalArrangement: Arrangement.Horizontal,
    hideTitleSemantics: Boolean,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .layoutId("navigationIcon")
                    .padding(start = FloatingAppBarHorizontalPadding),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon
                )
            }
            Box(
                modifier = Modifier
                    .layoutId("title")
                    .padding(horizontal = FloatingAppBarHorizontalPadding)
                    .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
            ) {
                ProvideTextStyle(value = titleTextStyle) {
                    CompositionLocalProvider(
                        LocalContentColor provides titleContentColor,
                        content = title,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .layoutId("actionIcons")
                    .padding(end = FloatingAppBarHorizontalPadding),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions,
                )
            }
        },
        measurePolicy = { measurables, constraints ->
            val navigationIconPlaceable = measurables
                .first { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0))
            val actionIconsPlaceable = measurables
                .first { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0))

            val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                    .coerceAtLeast(0)
            }
            val titlePlaceable = measurables
                .first { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

            val layoutHeight = FloatingAppBarHeight.roundToPx()

            layout(constraints.maxWidth, layoutHeight) {
                // Navigation icon
                navigationIconPlaceable.placeRelative(
                    x = 0,
                    y = (layoutHeight - navigationIconPlaceable.height) / 2,
                )

                // Title
                titlePlaceable.placeRelative(
                    x = when (titleHorizontalArrangement) {
                        Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
                        Arrangement.End -> constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
                        // Arrangement.Start.
                        // An TopAppBarTitleInset will make sure the title is offset in case the
                        // navigation icon is missing.
                        else -> max(FloatingAppBarTitleInset.roundToPx(), navigationIconPlaceable.width)
                    },
                    y = (layoutHeight - titlePlaceable.height) / 2,
                )

                // Action icons
                actionIconsPlaceable.placeRelative(
                    x = constraints.maxWidth - actionIconsPlaceable.width,
                    y = (layoutHeight - actionIconsPlaceable.height) / 2,
                )
            }
        },
    )
}

@Suppress("UNUSED")
fun TopAppBarDefaults.floatingScrollBehavior(
    state: TopAppBarState,
    canScroll: () -> Boolean = { true },
): TopAppBarScrollBehavior = FloatingScrollBehavior(state, canScroll)

private class FloatingScrollBehavior(
    override var state: TopAppBarState,
    val canScroll: () -> Boolean = { true },
) : TopAppBarScrollBehavior {

    override val isPinned = false
    override var nestedScrollConnection = object : NestedScrollConnection {

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (!canScroll()) return Offset.Zero
            state.contentOffset += consumed.y
            if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
                if (consumed.y == 0f && available.y > 0f) {
                    // Reset the total content offset to zero when scrolling all the way down.
                    // This will eliminate some float precision inaccuracies.
                    state.contentOffset = 0f
                }
            }
            state.heightOffset = state.heightOffset + consumed.y
            return Offset.Zero
        }
    }
}

private val FloatingAppBarHeight = 48.dp

private val FloatingAppBarTopMargin = 8.dp
private val FloatingAppBarHorizontalMargin = 16.dp
private val FloatingAppBarHorizontalPadding = 4.dp

// A title inset used to size a spacer when the navigation icon is missing.
private val FloatingAppBarTitleInset = 16.dp - FloatingAppBarHorizontalPadding

private val FloatingAppBarElevation = 6.dp
