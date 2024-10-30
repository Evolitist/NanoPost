package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evolitist.nanopost.domain.model.Likes
import com.evolitist.nanopost.presentation.ui.LikesViewModel
import com.evolitist.nanopost.presentation.ui.LocalSnackbarHost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun LikeButton(
    postId: String,
    modifier: Modifier = Modifier,
    initialLikes: Likes = Likes.Empty,
    viewModel: LikesViewModel = likesViewModel(initialLikes),
) {
    val likes by viewModel.getPostLikes(postId).collectAsStateWithLifecycle(initialLikes)

    val isLiked by remember {
        derivedStateOf { likes.liked }
    }
    val likesCount by remember {
        derivedStateOf { likes.likesCount }
    }

    val error by viewModel.errorState
    val snackbarHost = LocalSnackbarHost.current

    LaunchedEffect(error) {
        error?.let {
            snackbarHost?.showSnackbar(message = it.localizedMessage ?: "An error occurred")
        }
    }

    val buttonPadding = PaddingValues(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
    val buttonContent: @Composable RowScope.() -> Unit = {
        LikesButtonContent(isLiked = isLiked, likesCount = likesCount)
    }
    val onClick = {
        if (isLiked) {
            viewModel.unlikePost(postId)
        } else {
            viewModel.likePost(postId)
        }
    }
    if (isLiked) {
        FilledTonalButton(
            contentPadding = buttonPadding,
            modifier = modifier,
            onClick = onClick,
            content = buttonContent,
        )
    } else {
        TextButton(
            contentPadding = buttonPadding,
            modifier = modifier,
            onClick = onClick,
            content = buttonContent,
        )
    }
}

@Composable
fun likesViewModel(initialLikes: Likes = Likes.Empty): LikesViewModel {
    val context = LocalContext.current as? ViewModelStoreOwner
    return context?.let { hiltViewModel<LikesViewModel.Impl>(it) } ?: object : LikesViewModel {
        private val likesStateFlow = MutableStateFlow(initialLikes)

        override val errorState = remember { mutableStateOf<Throwable?>(null) }

        override fun getPostLikes(postId: String) = likesStateFlow

        override fun likePost(postId: String) {
            likesStateFlow.update { it.copy(liked = true, likesCount = it.likesCount.inc()) }
        }

        override fun unlikePost(postId: String) {
            likesStateFlow.update { it.copy(liked = false, likesCount = it.likesCount.dec()) }
        }
    }
}

@Composable
private fun RowScope.LikesButtonContent(
    isLiked: Boolean,
    likesCount: Int,
) {
    AnimatedContent(
        targetState = isLiked,
        label = "Like icon",
        transitionSpec = {
            val enterTransition = if (targetState) {
                expandIn(expandFrom = Alignment.Center)
            } else {
                EnterTransition.None
            }
            val exitTransition = if (targetState) {
                ExitTransition.None
            } else {
                shrinkOut(shrinkTowards = Alignment.Center)
            }
            enterTransition togetherWith exitTransition
        },
        modifier = Modifier.size(24.dp),
    ) { liked ->
        if (liked) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.FavoriteBorder,
                contentDescription = null,
            )
        }
    }
    Spacer(Modifier.size(8.dp))
    AnimatedContent(
        targetState = likesCount,
        label = "Likes count",
        transitionSpec = {
            val direction = if (targetState > initialState) {
                SlideDirection.Up
            } else {
                SlideDirection.Down
            }
            slideIntoContainer(direction) togetherWith slideOutOfContainer(direction)
        },
    ) { count ->
        Text(count.toString())
    }
}
