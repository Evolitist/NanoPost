package com.evolitist.nanopost.presentation.ui

import androidx.compose.runtime.Stable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.evolitist.nanopost.presentation.ui.auth.AuthRoute
import com.evolitist.nanopost.presentation.ui.create.post.CreatePostRoute
import com.evolitist.nanopost.presentation.ui.create.profile.CreateProfileRoute
import com.evolitist.nanopost.presentation.ui.feed.FeedRoute
import com.evolitist.nanopost.presentation.ui.image.ImageRoute
import com.evolitist.nanopost.presentation.ui.images.ImagesRoute
import com.evolitist.nanopost.presentation.ui.post.PostRoute
import com.evolitist.nanopost.presentation.ui.posts.PostsRoute
import com.evolitist.nanopost.presentation.ui.profile.ProfileRoute
import com.evolitist.nanopost.presentation.ui.subscribers.SubscribersRoute
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@Stable
class NanoPostNavigationActions(private val navController: NavHostController) {

    fun navigateUp() {
        navController.navigateUp()
    }

    fun switchToAuth() = navController.navigate(AuthRoute) {
        popUpTo<RootRoute> { inclusive = true }
    }

    fun switchToFeed() = navController.navigate(FeedRoute) {
        popUpTo<RootRoute> { inclusive = true }
    }

    fun navigateToProfile(id: String? = null) = navController.navigate(ProfileRoute(id))

    fun navigateToSubscribers(id: String? = null) = navController.navigate(SubscribersRoute(id))

    fun navigateToImages(id: String? = null) = navController.navigate(ImagesRoute(id))

    fun navigateToPosts(id: String? = null) = navController.navigate(PostsRoute(id))

    fun navigateToImage(id: String) = navController.navigate(ImageRoute(id))

    fun navigateToPost(id: String) = navController.navigate(PostRoute(id))

    fun navigateToCreatePost() = navController.navigate(CreatePostRoute)

    fun navigateToCreateProfile() = navController.navigate(CreateProfileRoute)
}

@OptIn(InternalSerializationApi::class)
fun <T : Any> NavBackStackEntry.matches(kclass: KClass<T>): Boolean {
    return kclass.serializer().hashCode() == destination.id
}

inline fun <reified T : Any> NavBackStackEntry.matches() = matches(T::class)
