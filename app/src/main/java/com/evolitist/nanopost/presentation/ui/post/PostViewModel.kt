package com.evolitist.nanopost.presentation.ui.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.nanopost.domain.usecase.DeletePostUseCase
import com.evolitist.nanopost.domain.usecase.GetPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class PostViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
) : ViewModel() {

    private val postIdFlow = MutableStateFlow<String?>(null)

    private val postStateFlow = postIdFlow
        .filterNotNull()
        .mapLatest { getPostUseCase(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val postState @Composable get() = postStateFlow.collectAsState()

    fun setPostId(postId: String) {
        postIdFlow.value = postId
    }

    fun onDeleteClick(postId: String, callback: () -> Unit) {
        viewModelScope.launch {
            deletePostUseCase(postId)
            callback()
        }
    }
}
