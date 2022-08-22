package com.evolitist.nanopost.presentation.ui

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.evolitist.nanopost.presentation.ui.NanoPostDestinationArgs.IdArg
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.AuthScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.CreatePostScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.CreateProfileScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.FeedScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.ImageScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.ImagesScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.LogoutScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.PostScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.PostsScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.ProfileScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.RootScreen
import com.evolitist.nanopost.presentation.ui.NanoPostScreens.SubscribersScreen

private object NanoPostScreens {
    const val RootScreen = "root"
    const val AuthScreen = "auth"
    const val LogoutScreen = "logout"
    const val FeedScreen = "feed"
    const val ProfileScreen = "profile"
    const val SubscribersScreen = "subscribers"
    const val ImagesScreen = "images"
    const val PostsScreen = "posts"
    const val ImageScreen = "image"
    const val PostScreen = "post"
    const val CreatePostScreen = "create_post"
    const val CreateProfileScreen = "create_profile"
}

object NanoPostDestinationArgs {
    const val IdArg = "id"
}

object NanoPostDestinations {
    const val RootRoute = RootScreen
    const val AuthRoute = AuthScreen
    const val LogoutRoute = LogoutScreen
    const val FeedRoute = FeedScreen
    const val ProfileRoute = "$ProfileScreen?$IdArg=$IdArg"
    const val SubscribersRoute = "$SubscribersScreen?$IdArg=$IdArg"
    const val ImagesRoute = "$ImagesScreen?$IdArg=$IdArg"
    const val PostsRoute = "$PostsScreen?$IdArg=$IdArg"
    const val ImageRoute = "$ImageScreen/$IdArg"
    const val PostRoute = "$PostScreen/$IdArg"
    const val CreatePostRoute = CreatePostScreen
    const val CreateProfileRoute = CreateProfileScreen
}

@Stable
class NanoPostNavigationActions(private val navController: NavHostController) {

    fun navigateUp() {
        navController.navigateUp()
    }

    fun switchToAuth() = navController.navigate(NanoPostDestinations.AuthRoute) {
        popUpTo(navController.graph.id)
    }

    fun switchToFeed() = navController.navigate(NanoPostDestinations.FeedRoute) {
        popUpTo(navController.graph.id)
    }

    fun openLogoutDialog() = navController.navigate(NanoPostDestinations.LogoutRoute)

    fun navigateToProfile(id: String? = null) = navController.navigate(
        ProfileScreen.let {
            if (id != null) "$it?$IdArg=$id" else it
        }
    )

    fun navigateToSubscribers(id: String? = null) = navController.navigate(
        SubscribersScreen.let {
            if (id != null) "$it?$IdArg=$id" else it
        }
    )

    fun navigateToImages(id: String? = null) = navController.navigate(
        ImagesScreen.let {
            if (id != null) "$it?$IdArg=$id" else it
        }
    )

    fun navigateToPosts(id: String? = null) = navController.navigate(
        PostsScreen.let {
            if (id != null) "$it?$IdArg=$id" else it
        }
    )

    fun navigateToImage(id: String) = navController.navigate("$ImageScreen?$IdArg=$id")

    fun navigateToPost(id: String) = navController.navigate("$PostScreen?$IdArg=$id")

    fun navigateToCreatePost() = navController.navigate(NanoPostDestinations.CreatePostRoute)

    fun navigateToCreateProfile() = navController.navigate(NanoPostDestinations.CreateProfileRoute)
}
