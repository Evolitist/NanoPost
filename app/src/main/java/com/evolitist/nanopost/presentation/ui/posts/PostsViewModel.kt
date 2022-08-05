package com.evolitist.nanopost.presentation.ui.posts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.evolitist.nanopost.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@Stable
@HiltViewModel
class PostsViewModel @Inject constructor(
    getPostsUseCase: GetPostsUseCase,
) : ViewModel() {

    private val profileIdFlow = MutableStateFlow<String?>(null)

    private val postsPagingDataFlow = profileIdFlow
        .flatMapLatest { getPostsUseCase(it) }
        .cachedIn(viewModelScope)

    val postsPagingItems @Composable get() = postsPagingDataFlow.collectAsLazyPagingItems()

    fun setProfileId(profileId: String?) {
        profileIdFlow.value = profileId
    }
}
