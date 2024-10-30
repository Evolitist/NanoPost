package com.evolitist.nanopost.presentation.ui.posts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.evolitist.nanopost.domain.usecase.GetPostsUseCase
import com.evolitist.nanopost.presentation.extensions.filterData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPostsUseCase: GetPostsUseCase,
) : ViewModel() {

    private val profileId = savedStateHandle.toRoute<PostsRoute>().id

    val postsPagingDataFlow = flow { emitAll(getPostsUseCase(profileId)) }
        .filterData { !it.text.isNullOrBlank() || it.images.isNotEmpty() }
        .cachedIn(viewModelScope)
}
