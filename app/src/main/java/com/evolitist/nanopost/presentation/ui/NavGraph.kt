package com.evolitist.nanopost.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.evolitist.nanopost.presentation.ui.NanoPostDestinationArgs.IdArg
import com.evolitist.nanopost.presentation.ui.auth.AuthScreen
import com.evolitist.nanopost.presentation.ui.create.post.CreatePostScreen
import com.evolitist.nanopost.presentation.ui.create.profile.CreateProfileScreen
import com.evolitist.nanopost.presentation.ui.feed.FeedScreen
import com.evolitist.nanopost.presentation.ui.image.ImageScreen
import com.evolitist.nanopost.presentation.ui.images.ImagesScreen
import com.evolitist.nanopost.presentation.ui.post.PostScreen
import com.evolitist.nanopost.presentation.ui.posts.PostsScreen
import com.evolitist.nanopost.presentation.ui.profile.ProfileScreen
import com.evolitist.nanopost.presentation.ui.subscribers.SubscribersScreen
import com.evolitist.nanopost.presentation.ui.view.AlertDialogContent
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun AppNavGraph() {
    val activityViewModel = viewModel<MainViewModel>()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val navigationActions = remember(navController) {
        NanoPostNavigationActions(navController)
    }

    val showAuth by activityViewModel.showAuthFlow.collectAsState()

    LaunchedEffect(showAuth) {
        when (showAuth) {
            true -> navigationActions.switchToAuth()
            false -> navigationActions.switchToFeed()
            else -> Unit
        }
    }

    Scaffold { innerPadding ->
        val slideDistance = rememberSlideDistance()
        AnimatedNavHost(
            navController = navController,
            startDestination = NanoPostDestinations.RootRoute,
            enterTransition = { materialSharedAxisXIn(true, slideDistance) },
            exitTransition = { materialSharedAxisXOut(true, slideDistance) },
            popEnterTransition = { materialSharedAxisXIn(false, slideDistance) },
            popExitTransition = { materialSharedAxisXOut(false, slideDistance) },
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(NanoPostDestinations.RootRoute) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            composable(NanoPostDestinations.AuthRoute) {
                AuthScreen()
            }

            dialog(NanoPostDestinations.LogoutRoute) {
                AlertDialogContent(
                    title = { Text("Logout") },
                    text = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        TextButton(
                            onClick = activityViewModel::logout,
                            content = { Text("Confirm") },
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = navController::navigateUp,
                            content = { Text("Cancel") },
                        )
                    },
                )
            }

            authorizedRoutes(navigationActions)
        }
    }
}

fun NavGraphBuilder.authorizedRoutes(navigationActions: NanoPostNavigationActions) {
    composable(NanoPostDestinations.FeedRoute) {
        FeedScreen(
            onCreatePostClick = navigationActions::navigateToCreatePost,
            onCreateProfileClick = navigationActions::navigateToCreateProfile,
            onPostClick = navigationActions::navigateToPost,
            onImageClick = navigationActions::navigateToImage,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable(
        route = NanoPostDestinations.ProfileRoute,
        arguments = listOf(
            navArgument(IdArg) {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        ProfileScreen(
            profileId = backStackEntry.arguments?.getString(IdArg),
            onCloseClick = navigationActions::navigateUp,
            onLogoutClick = navigationActions::openLogoutDialog,
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

    composable(
        route = NanoPostDestinations.SubscribersRoute,
        arguments = listOf(
            navArgument(IdArg) {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        SubscribersScreen(
            profileId = backStackEntry.arguments?.getString(IdArg),
            onCloseClick = navigationActions::navigateUp,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable(
        route = NanoPostDestinations.ImagesRoute,
        arguments = listOf(
            navArgument(IdArg) {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        ImagesScreen(
            profileId = backStackEntry.arguments?.getString(IdArg),
            onCloseClick = navigationActions::navigateUp,
            onImageClick = navigationActions::navigateToImage,
        )
    }

    composable(
        route = NanoPostDestinations.PostsRoute,
        arguments = listOf(
            navArgument(IdArg) {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        PostsScreen(
            profileId = backStackEntry.arguments?.getString(IdArg),
            onCloseClick = navigationActions::navigateUp,
            onPostClick = navigationActions::navigateToPost,
            onImageClick = navigationActions::navigateToImage,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable(
        route = NanoPostDestinations.ImageRoute,
        arguments = listOf(
            navArgument(IdArg) { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        ImageScreen(
            imageId = backStackEntry.arguments!!.getString(IdArg)!!,
            onCloseClick = navigationActions::navigateUp,
        )
    }

    composable(
        route = NanoPostDestinations.PostRoute,
        arguments = listOf(
            navArgument(IdArg) { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        PostScreen(
            postId = backStackEntry.arguments!!.getString(IdArg)!!,
            onCloseClick = navigationActions::navigateUp,
            onImageClick = navigationActions::navigateToImage,
            onProfileClick = navigationActions::navigateToProfile,
        )
    }

    composable(NanoPostDestinations.CreatePostRoute) {
        CreatePostScreen(
            onCloseClick = navigationActions::navigateUp,
        )
    }

    composable(NanoPostDestinations.CreateProfileRoute) {
        CreateProfileScreen(
            onCloseClick = navigationActions::navigateUp,
        )
    }
}
