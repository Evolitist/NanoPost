package com.evolitist.nanopost.presentation.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.ui.auth.AuthScreen
import com.evolitist.nanopost.presentation.ui.create.post.CreatePostScreen
import com.evolitist.nanopost.presentation.ui.create.profile.CreateProfileScreen
import com.evolitist.nanopost.presentation.ui.feed.FeedScreen
import com.evolitist.nanopost.presentation.ui.image.ImageScreen
import com.evolitist.nanopost.presentation.ui.images.ImagesScreen
import com.evolitist.nanopost.presentation.ui.post.PostScreen
import com.evolitist.nanopost.presentation.ui.profile.ProfileScreen
import com.evolitist.nanopost.presentation.ui.view.AlertDialogContent
import com.evolitist.nanopost.presentation.ui.view.ModalBottomSheetLayout
import com.evolitist.nanopost.presentation.ui.view.NavigationBar
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@Immutable
enum class Screen(
    val route: String,
    @StringRes val titleStringId: Int,
) {
    NavGraph("nav_graph", 0),
    Root("root", 0),
    Auth("auth", R.string.auth),
    Logout("logout", R.string.logout),
    FeedGraph("feed_graph", R.string.feed),
    Feed("feed", R.string.feed),
    SearchGraph("search_graph", R.string.search),
    Search("search", R.string.search),
    ProfileGraph("profile_graph", R.string.profile),
    Profile("profile", R.string.profile),
    Images("images", R.string.images),
    CreatePost("create_post", R.string.create_post),
    CreateProfile("create_profile", R.string.create_profile),
    Post("post", R.string.post),
    Image("image", R.string.image);

    companion object {
        val hideNavigationBar = setOf(Auth, CreatePost, Image, Post)
        val bottomNavigationItems = mapOf(
            FeedGraph to Icons.Rounded.DynamicFeed,
            SearchGraph to Icons.Rounded.Search,
            ProfileGraph to Icons.Rounded.AccountCircle,
        )
    }

    fun matches(route: String?): Boolean {
        return route?.split("/", "?")?.first() == this.route
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavGraph() {
    val activityViewModel = hiltViewModel<MainViewModel>()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val authStatus by activityViewModel.appStatusFlow.collectAsState()

    LaunchedEffect(authStatus) {
        when (authStatus) {
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
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    Screen.bottomNavigationItems.forEach { (screen, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(stringResource(screen.titleStringId)) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route?.startsWith(screen.route) == true
                            } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.NavGraph.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                route = Screen.NavGraph.route,
                startDestination = Screen.Root.route,
                modifier = Modifier.padding(padding),
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

                commonRoutes(navController)

                navigation(
                    route = Screen.FeedGraph.route,
                    startDestination = Screen.Feed.route,
                ) {
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
                            onProfileClick = {
                                navController.navigate("${Screen.Profile.route}?id=$it")
                            },
                        )
                    }
                }

                navigation(
                    route = Screen.SearchGraph.route,
                    startDestination = Screen.Search.route,
                ) {
                    composable(Screen.Search.route) {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            Text("NYI")
                        }
                    }
                }

                navigation(
                    route = Screen.ProfileGraph.route,
                    startDestination = Screen.Profile.route,
                ) {
                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            profileId = null,
                            onCloseClick = {
                                navController.navigateUp()
                            },
                            onLogoutClick = {
                                navController.navigate(Screen.Logout.route)
                            },
                            onImagesClick = {
                                navController.navigate(Screen.Images.route)
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
                }
            }
        }
    }
}

fun NavGraphBuilder.commonRoutes(navController: NavController) {
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
            onImagesClick = { profileId ->
                navController.navigate(
                    Screen.Images.route + profileId?.let { "?id=$it" }.orEmpty()
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
