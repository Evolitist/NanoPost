package com.evolitist.nanopost.presentation.ui.posts

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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

    val postsPagingDataFlow = profileIdFlow
        .flatMapLatest { getPostsUseCase(it) }
        .cachedIn(viewModelScope)

    fun setProfileId(profileId: String?) {
        profileIdFlow.value = profileId
    }
}
