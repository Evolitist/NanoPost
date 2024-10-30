package com.evolitist.nanopost.presentation.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.nanopost.domain.model.Likes
import com.evolitist.nanopost.domain.usecase.GetPostLikesUseCase
import com.evolitist.nanopost.domain.usecase.LikePostUseCase
import com.evolitist.nanopost.domain.usecase.UnlikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pro.respawn.kmmutils.coroutines.launchCatching
import javax.inject.Inject

@Stable
interface LikesViewModel {

    val errorState: State<Throwable?>

    fun getPostLikes(postId: String): Flow<Likes>

    fun likePost(postId: String)

    fun unlikePost(postId: String)

    @Stable
    @HiltViewModel
    class Impl @Inject constructor(
        private val getPostLikesUseCase: GetPostLikesUseCase,
        private val likePostUseCase: LikePostUseCase,
        private val unlikePostUseCase: UnlikePostUseCase,
    ) : ViewModel(), LikesViewModel {

        private val _errorState = mutableStateOf<Throwable?>(null)
        override val errorState = _errorState

        override fun getPostLikes(postId: String) = getPostLikesUseCase(postId)

        override fun likePost(postId: String) {
            viewModelScope.launchCatching(
                onError = { _errorState.value = it },
            ) {
                likePostUseCase(postId)
            }
        }

        override fun unlikePost(postId: String) {
            viewModelScope.launchCatching(
                onError = { _errorState.value = it },
            ) {
                unlikePostUseCase(postId)
            }
        }
    }

    companion object : LikesViewModel {

        private val likesFlow = MutableStateFlow(Likes.Empty)
        override val errorState = mutableStateOf<Throwable?>(null)

        override fun getPostLikes(postId: String) = likesFlow

        override fun likePost(postId: String) {
            likesFlow.value = Likes(true, 1)
        }

        override fun unlikePost(postId: String) {
            likesFlow.value = Likes(false, 0)
        }
    }
}
