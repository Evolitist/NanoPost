package com.evolitist.nanopost.presentation.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
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
    Feed("feed", R.string.feed),
    Search("search", R.string.search),
    Profile("profile", R.string.profile),
    Images("images", R.string.images),
    CreatePost("create_post", R.string.create_post),
    CreateProfile("create_profile", R.string.create_profile),
    Post("post", R.string.post),
    Image("image", R.string.image);

    companion object {
        val hideNavigationBar = setOf(Auth, CreatePost, Image, Post)
        val bottomNavigationItems = mapOf(
            Feed to Icons.Rounded.DynamicFeed,
            Search to Icons.Rounded.Search,
            Profile to Icons.Rounded.AccountCircle,
        )
    }

    fun matches(route: String?): Boolean {
        return route?.split("/")?.first() == this.route
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

    LaunchedEffect(navBackStackEntry) {
        println(navBackStackEntry?.destination?.hierarchy?.joinToString { it.route.orEmpty() })
    }

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
                            selected = currentDestination?.hierarchy
                                ?.any { it.route?.startsWith(screen.route) == true } == true,
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

                composable(
                    route = Screen.Feed.route,
                ) {
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
                            navController.navigate("${Screen.Profile.route}?profileId=$it")
                        },
                    )
                }

                composable(
                    route = Screen.Search.route,
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Text("NYI")
                    }
                }

                composable(
                    route = "${Screen.Profile.route}?profileId={profileId}",
                    arguments = listOf(
                        navArgument("profileId") {
                            type = NavType.StringType
                            nullable = true
                        }
                    ),
                ) { backStackEntry ->
                    ProfileScreen(
                        profileId = backStackEntry.arguments?.getString("profileId"),
                        onCloseClick = {
                            navController.navigateUp()
                        },
                        onLogoutClick = {
                            navController.navigate(Screen.Logout.route)
                        },
                        onImagesClick = { profileId ->
                            navController.navigate(
                                Screen.Images.route + profileId?.let { "?profileId=$it" }.orEmpty()
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
                            navController.navigate("${Screen.Profile.route}?profileId=$it")
                        },
                    )
                }

                composable(
                    route = "${Screen.Images.route}?profileId={profileId}",
                    arguments = listOf(
                        navArgument("profileId") {
                            type = NavType.StringType
                            nullable = true
                        }
                    ),
                ) { backStackEntry ->
                    ImagesScreen(
                        profileId = backStackEntry.arguments?.getString("profileId"),
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
                    route = "${Screen.Post.route}/{postId}",
                    arguments = listOf(
                        navArgument("postId") { type = NavType.StringType }
                    ),
                ) { backStackEntry ->
                    PostScreen(
                        postId = backStackEntry.arguments!!.getString("postId")!!,
                        onCloseClick = {
                            navController.navigateUp()
                        },
                        onImageClick = {
                            navController.navigate("${Screen.Image.route}/$it")
                        },
                        onProfileClick = {
                            navController.navigate("${Screen.Profile.route}?profileId=$it")
                        },
                    )
                }

                composable(
                    route = "${Screen.Image.route}/{imageId}",
                    arguments = listOf(
                        navArgument("imageId") { type = NavType.StringType }
                    ),
                ) { backStackEntry ->
                    ImageScreen(
                        imageId = backStackEntry.arguments!!.getString("imageId")!!,
                        onCloseClick = {
                            navController.navigateUp()
                        },
                    )
                }
            }
        }
    }
}
