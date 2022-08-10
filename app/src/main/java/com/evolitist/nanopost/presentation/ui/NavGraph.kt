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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
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
import com.evolitist.nanopost.presentation.ui.view.ModalBottomSheetLayout
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

enum class Screen(
    val route: String,
) {
    NavGraph("nav_graph"),
    Root("root"),
    Auth("auth"),
    Logout("logout"),
    Feed("feed"),
    Search("search"),
    Profile("profile"),
    Subscribers("subscribers"),
    Images("images"),
    Posts("posts"),
    CreatePost("create_post"),
    CreateProfile("create_profile"),
    Post("post"),
    Image("image");
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun AppNavGraph() {
    val activityViewModel = viewModel<MainViewModel>()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)

    val showAuth by activityViewModel.showAuthFlow.collectAsState()

    LaunchedEffect(showAuth) {
        when (showAuth) {
            true -> navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.NavGraph.route)
            }
            false -> navController.navigate(Screen.Feed.route) {
                popUpTo(Screen.NavGraph.route)
            }
            else -> Unit
        }
    }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
    ) {
        Scaffold { innerPadding ->
            val slideDistance = rememberSlideDistance()
            AnimatedNavHost(
                navController = navController,
                route = Screen.NavGraph.route,
                startDestination = Screen.Root.route,
                enterTransition = { materialSharedAxisXIn(true, slideDistance) },
                exitTransition = { materialSharedAxisXOut(true, slideDistance) },
                popEnterTransition = { materialSharedAxisXIn(false, slideDistance) },
                popExitTransition = { materialSharedAxisXOut(false, slideDistance) },
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(
                    route = Screen.Root.route,
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                composable(
                    route = Screen.Auth.route,
                ) {
                    AuthScreen()
                }

                dialog(
                    route = Screen.Logout.route,
                ) {
                    AlertDialogContent(
                        title = { Text("Logout") },
                        text = { Text("Are you sure you want to logout?") },
                        confirmButton = {
                            TextButton(
                                onClick = { activityViewModel.logout() },
                                content = { Text("Confirm") },
                            )
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { navController.navigateUp() },
                                content = { Text("Cancel") },
                            )
                        },
                    )
                }

                authorizedRoutes(navController)
            }
        }
    }
}

fun NavGraphBuilder.authorizedRoutes(navController: NavController) {
    composable(Screen.Feed.route) {
        FeedScreen(
            onCreatePostClick = {
                navController.navigate(Screen.CreatePost.route)
            },
            onCreateProfileClick = {
                navController.navigate(Screen.CreateProfile.route)
            },
            onPostClick = {
                navController.navigate("${Screen.Post.route}/$it")
            },
            onImageClick = {
                navController.navigate("${Screen.Image.route}/$it")
            },
            onProfileClick = { profileId ->
                navController.navigate(
                    Screen.Profile.route + profileId?.let { "?id=$it" }.orEmpty()
                )
            },
        )
    }

    composable(
        route = "${Screen.Profile.route}?id={id}",
        arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        ProfileScreen(
            profileId = backStackEntry.arguments?.getString("id"),
            onCloseClick = {
                navController.navigateUp()
            },
            onLogoutClick = {
                navController.navigate(Screen.Logout.route)
            },
            onSubscribersClick = { profileId ->
                navController.navigate(
                    Screen.Subscribers.route + profileId?.let { "?id=$it" }.orEmpty()
                )
            },
            onImagesClick = { profileId ->
                navController.navigate(
                    Screen.Images.route + profileId?.let { "?id=$it" }.orEmpty()
                )
            },
            onPostsClick = { profileId ->
                navController.navigate(
                    Screen.Posts.route + profileId?.let { "?id=$it" }.orEmpty()
                )
            },
            onCreatePostClick = {
                navController.navigate(Screen.CreatePost.route)
            },
            onCreateProfileClick = {
                navController.navigate(Screen.CreateProfile.route)
            },
            onPostClick = {
                navController.navigate("${Screen.Post.route}/$it")
            },
            onImageClick = {
                navController.navigate("${Screen.Image.route}/$it")
            },
            onProfileClick = {
                navController.navigate("${Screen.Profile.route}?id=$it")
            },
        )
    }

    composable(
        route = "${Screen.Subscribers.route}?id={id}",
        arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        SubscribersScreen(
            profileId = backStackEntry.arguments?.getString("id"),
            onCloseClick = {
                navController.navigateUp()
            },
            onProfileClick = {
                navController.navigate("${Screen.Profile.route}?id=$it")
            },
        )
    }

    composable(
        route = "${Screen.Images.route}?id={id}",
        arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        ImagesScreen(
            profileId = backStackEntry.arguments?.getString("id"),
            onCloseClick = {
                navController.navigateUp()
            },
            onImageClick = {
                navController.navigate("${Screen.Image.route}/$it")
            },
        )
    }

    composable(
        route = "${Screen.Posts.route}?id={id}",
        arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
                nullable = true
            }
        ),
    ) { backStackEntry ->
        PostsScreen(
            profileId = backStackEntry.arguments?.getString("id"),
            onCloseClick = {
                navController.navigateUp()
            },
            onPostClick = {
                navController.navigate("${Screen.Post.route}/$it")
            },
            onImageClick = {
                navController.navigate("${Screen.Image.route}/$it")
            },
            onProfileClick = {
                navController.navigate("${Screen.Profile.route}?id=$it")
            },
        )
    }

    composable(
        route = Screen.CreatePost.route,
    ) {
        CreatePostScreen(
            onCloseClick = {
                navController.navigateUp()
            },
        )
    }

    composable(
        route = Screen.CreateProfile.route,
    ) {
        CreateProfileScreen(
            onCloseClick = {
                navController.navigateUp()
            },
        )
    }

    composable(
        route = "${Screen.Post.route}/{id}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        PostScreen(
            postId = backStackEntry.arguments!!.getString("id")!!,
            onCloseClick = {
                navController.navigateUp()
            },
            onImageClick = {
                navController.navigate("${Screen.Image.route}/$it")
            },
            onProfileClick = {
                navController.navigate("${Screen.Profile.route}?id=$it")
            },
        )
    }

    composable(
        route = "${Screen.Image.route}/{id}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        ImageScreen(
            imageId = backStackEntry.arguments!!.getString("id")!!,
            onCloseClick = {
                navController.navigateUp()
            },
        )
    }
}
