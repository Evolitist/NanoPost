package com.evolitist.nanopost.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlin.reflect.KType

val LocalSharedTransitionParams = staticCompositionLocalOf<SharedTransitionParams> {
    error("SharedTransitionParams not provided in current route")
}

@Composable
fun DefaultLocalSharedTransitionParams(content: @Composable (Modifier) -> Unit) {
    SharedTransitionScope {
        AnimatedVisibility(visible = true) {
            content(it)
        }
    }
}

fun Modifier.sharedBounds(key: Any) = this.composed {
    val sharedTransitionParams = LocalSharedTransitionParams.current

    with(sharedTransitionParams.sharedTransitionScope) {
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = sharedTransitionParams.animatedVisibilityScope,
        )
    }
}

context(SharedTransitionScope)
inline fun <reified T : Any> NavGraphBuilder.composable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?)? = null,
    noinline exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?)? = null,
    noinline popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?)? = enterTransition,
    noinline popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?)? = exitTransition,
    noinline sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards SizeTransform?)? = null,
    crossinline content: @Composable AnimatedContentScope.(T) -> Unit,
) = composable<T>(
    typeMap = typeMap,
    deepLinks = deepLinks,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition,
    sizeTransform = sizeTransform,
) { navBackStackEntry: NavBackStackEntry ->
    val sharedTransitionParams = remember {
        SharedTransitionParams(
            sharedTransitionScope = this@SharedTransitionScope,
            animatedVisibilityScope = this,
        )
    }
    CompositionLocalProvider(LocalSharedTransitionParams provides sharedTransitionParams) {
        content(navBackStackEntry.toRoute())
    }
}

class SharedTransitionParams(
    val sharedTransitionScope: SharedTransitionScope,
    val animatedVisibilityScope: AnimatedVisibilityScope,
)
