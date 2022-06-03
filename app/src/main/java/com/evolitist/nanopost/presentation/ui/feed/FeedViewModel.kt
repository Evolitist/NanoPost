package com.evolitist.nanopost.presentation.ui.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.evolitist.nanopost.domain.usecase.GetFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Stable
@HiltViewModel
class FeedViewModel @Inject constructor(
    getFeedUseCase: GetFeedUseCase,
) : ViewModel() {

    private val feedPagingDataFlow = getFeedUseCase()
        .cachedIn(viewModelScope)

    val feedPagingItems @Composable get() = feedPagingDataFlow.collectAsLazyPagingItems()
}
