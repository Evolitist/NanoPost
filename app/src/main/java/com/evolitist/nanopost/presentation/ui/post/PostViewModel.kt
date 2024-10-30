package com.evolitist.nanopost.presentation.ui.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.evolitist.nanopost.domain.usecase.DeletePostUseCase
import com.evolitist.nanopost.domain.usecase.GetPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPostUseCase: GetPostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
) : ViewModel() {

    private val postId = savedStateHandle.toRoute<PostRoute>().id

    val postStateFlow = flow {
        emit(getPostUseCase(postId))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    fun onDeleteClick(callback: () -> Unit) {
        viewModelScope.launch {
            deletePostUseCase(postId)
            callback()
        }
    }
}
