package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * ![Small top app bar image](https://developer.android.com/images/reference/androidx/compose/material3/small-top-app-bar.png)
 *
 * Material Design small top app bar.
 *
 * The top app bar displays information and actions relating to the current screen.
 *
 * This FloatingTopAppBar has slots for a title, navigation icon, and actions.
 *
 * @param title the title to be displayed in the top app bar
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon The navigation icon displayed at the start of the top app bar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 */
@Composable
fun FloatingTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    shape: Shape = CircleShape,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    // Sets the app bar's height offset to collapse the entire bar's height when content is
    // scrolled.
    val heightOffsetLimit = with(LocalDensity.current) {
        val topInset = windowInsets.asPaddingValues(this).calculateTopPadding()
        -(FloatingAppBarHeight + FloatingAppBarTopMargin + topInset).toPx()
    }
    SideEffect {
        if (scrollBehavior?.state?.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior?.state?.heightOffsetLimit = heightOffsetLimit
        }
    }

    // Wrap the given actions in a Row.
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }

    // Compose a Surface with a FloatingAppBarLayout content. The offset of the app bar will be
    // determined by the current scroll-state offset.
    Surface(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .padding(horizontal = FloatingAppBarHorizontalMargin)
            .padding(top = FloatingAppBarTopMargin)
            .offset {
                IntOffset(
                    x = 0,
                    y = scrollBehavior?.state?.heightOffset?.roundToInt() ?: 0,
                )
            },
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        shadowElevation = FloatingAppBarElevation,
    ) {
        FloatingAppBarLayout(
            modifier = Modifier,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
 * @param hideTitleSemantics hides the title node from the semantic tree. This is needed to avoid
 * having multiple titles visible to accessibility services at the same time, when animating between
 * collapsed / expanded states.
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
/**
 * Returns a [TopAppBarScrollBehavior]. A top app bar that is set up with this
 * [TopAppBarScrollBehavior] will immediately collapse when the content is pulled up, and will
 * immediately appear when the content is pulled down.
 *
 * @param state the state object to be used to control or observe the top app bar's scroll
 * state. See [rememberTopAppBarState] for a state that is remembered across compositions.
 * @param canScroll a callback used to determine whether scroll events are to be
 * handled by this [FloatingScrollBehavior]
 * @param snapAnimationSpec an optional [AnimationSpec] that defines how the top app bar snaps
 * to either fully collapsed or fully extended state when a fling or a drag scrolled it into an
 * intermediate position
 * @param flingAnimationSpec an optional [DecayAnimationSpec] that defined how to fling the top
 * app bar when the user flings the app bar itself, or the content below it
 */
@Suppress("UNUSED")
@Composable
fun TopAppBarDefaults.floatingScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
): TopAppBarScrollBehavior = FloatingScrollBehavior(
    state = state,
    snapAnimationSpec = snapAnimationSpec,
    flingAnimationSpec = flingAnimationSpec,
    canScroll = canScroll,
)

/**
 * A [TopAppBarScrollBehavior] that adjusts its properties to affect the colors and height of a top
 * app bar.
 *
 * A top app bar that is set up with this [TopAppBarScrollBehavior] will immediately collapse when
 * the nested content is pulled up, and will immediately appear when the content is pulled down.
 *
 * @param state a [TopAppBarState]
 * @param snapAnimationSpec an optional [AnimationSpec] that defines how the top app bar snaps to
 * either fully collapsed or fully extended state when a fling or a drag scrolled it into an
 * intermediate position
 * @param flingAnimationSpec an optional [DecayAnimationSpec] that defined how to fling the top app
 * bar when the user flings the app bar itself, or the content below it
 * @param canScroll a callback used to determine whether scroll events are to be
 * handled by this [FloatingScrollBehavior]
 */
private class FloatingScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
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

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val superConsumed = super.onPostFling(consumed, available)
            return superConsumed + settleAppBar(
                state,
                available.y,
                flingAnimationSpec,
                snapAnimationSpec,
            )
        }
    }
}

/**
 * Settles the app bar by flinging, in case the given velocity is greater than zero, and snapping
 * after the fling settles.
 */
private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?,
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        ).animateDecay(flingAnimationSpec) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset
            state.heightOffset = initialHeightOffset + delta
            val consumed = abs(initialHeightOffset - state.heightOffset)
            lastValue = value
            remainingVelocity = this.velocity
            // avoid rounding errors and stop if anything is unconsumed
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec,
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}

private val FloatingAppBarHeight = 48.dp

private val FloatingAppBarTopMargin = 8.dp
private val FloatingAppBarHorizontalMargin = 16.dp
private val FloatingAppBarHorizontalPadding = 4.dp

// A title inset used to size a spacer when the navigation icon is missing.
private val FloatingAppBarTitleInset = 16.dp - FloatingAppBarHorizontalPadding

private val FloatingAppBarElevation = 6.dp
