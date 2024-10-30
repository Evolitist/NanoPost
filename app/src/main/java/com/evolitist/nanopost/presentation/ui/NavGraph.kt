package com.evolitist.nanopost.presentation.ui

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.evolitist.nanopost.presentation.navigation.composable
import com.evolitist.nanopost.presentation.ui.auth.AuthRoute
import com.evolitist.nanopost.presentation.ui.create.post.CreatePostRoute
import com.evolitist.nanopost.presentation.ui.create.post.CreatePostScreen
import com.evolitist.nanopost.presentation.ui.create.profile.CreateProfileRoute
import com.evolitist.nanopost.presentation.ui.create.profile.CreateProfileScreen
import com.evolitist.nanopost.presentation.ui.feed.FeedRoute
import com.evolitist.nanopost.presentation.ui.image.ImageRoute
import com.evolitist.nanopost.presentation.ui.images.ImagesRoute
import com.evolitist.nanopost.presentation.ui.post.PostRoute
import com.evolitist.nanopost.presentation.ui.posts.PostsRoute
import com.evolitist.nanopost.presentation.ui.profile.ProfileRoute
import com.evolitist.nanopost.presentation.ui.subscribers.SubscribersRoute
import kotlinx.serialization.Serializable
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@Serializable
data object RootRoute

@Composable
fun AppNavGraph() {
    val activityViewModel = hiltViewModel<MainViewModel>(LocalContext.current as ViewModelStoreOwner)
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        NanoPostNavigationActions(navController)
    }

    val showAuth by activityViewModel.showAuthFlow.collectAsStateWithLifecycle()

    LaunchedEffect(showAuth) {
        when (showAuth) {
            true -> navigationActions.switchToAuth()
            false -> navigationActions.switchToFeed()
            else -> Unit
        }
    }

    val slideDistance = rememberSlideDistance()
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = RootRoute,
            enterTransition = { materialSharedAxisXIn(true, slideDistance) },
            exitTransition = {
                if (targetState.matches<CreatePostRoute>() || targetState.matches<CreateProfileRoute>()) {
                    ExitTransition.KeepUntilTransitionsFinished
                } else {
                    materialSharedAxisXOut(true, slideDistance)
                }
            },
            popEnterTransition = {
                if (initialState.matches<CreatePostRoute>() || initialState.matches<CreateProfileRoute>()) {
                    fadeIn(animationSpec = tween(700))
                } else {
                    materialSharedAxisXIn(false, slideDistance)
                }
            },
            popExitTransition = { materialSharedAxisXOut(false, slideDistance) },
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<RootRoute> {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            composable<AuthRoute> {
                AuthRoute()
            }

            authorizedRoutes(
                navigationActions = navigationActions,
                onLogout = activityViewModel::logout,
            )
        }
    }
}

context(SharedTransitionScope)
fun NavGraphBuilder.authorizedRoutes(
    navigationActions: NanoPostNavigationActions,
    onLogout: () -> Unit,
) {
    composable<FeedRoute> {
        FeedRoute(
            onCreatePostClick = navigationActions::navigateToCreatePost,
            onCreateProfileClick = navigationActions::navigateToCreateProfile,
            onPostClick = navigationActions::navigateToPost,
            onImageClick = navigationActions::navigateToImage,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable<ProfileRoute> {
        ProfileRoute(
            profileId = it.id,
            onCloseClick = navigationActions::navigateUp,
            onLogoutClick = onLogout,
            onSubscribersClick = navigationActions::navigateToSubscribers,
            onImagesClick = navigationActions::navigateToImages,
            onPostsClick = navigationActions::navigateToPosts,
            onCreatePostClick = navigationActions::navigateToCreatePost,
            onCreateProfileClick = navigationActions::navigateToCreateProfile,
            onPostClick = navigationActions::navigateToPost,
            onImageClick = navigationActions::navigateToImage,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable<SubscribersRoute> {
        SubscribersRoute(
            onCloseClick = navigationActions::navigateUp,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable<ImagesRoute> {
        ImagesRoute(
            onCloseClick = navigationActions::navigateUp,
            onImageClick = navigationActions::navigateToImage,
        )
    }

    composable<PostsRoute> {
        PostsRoute(
            onCloseClick = navigationActions::navigateUp,
            onPostClick = navigationActions::navigateToPost,
            onImageClick = navigationActions::navigateToImage,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable<ImageRoute> {
        ImageRoute(
            onCloseClick = navigationActions::navigateUp,
        )
    }

    composable<PostRoute> {
        PostRoute(
            onCloseClick = navigationActions::navigateUp,
            onImageClick = navigationActions::navigateToImage,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable<CreatePostRoute>(
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) },
        popEnterTransition = { fadeIn(animationSpec = tween(700)) },
        popExitTransition = { fadeOut(animationSpec = tween(700)) },
    ) {
        CreatePostScreen(
            onCloseClick = navigationActions::navigateUp,
        )
    }

    composable<CreateProfileRoute>(
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) },
        popEnterTransition = { fadeIn(animationSpec = tween(700)) },
        popExitTransition = { fadeOut(animationSpec = tween(700)) },
    ) {
        CreateProfileScreen(
            onCloseClick = navigationActions::navigateUp,
        )
    }
}
