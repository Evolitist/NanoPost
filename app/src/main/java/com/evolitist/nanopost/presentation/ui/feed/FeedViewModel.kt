package com.evolitist.nanopost.presentation.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.evolitist.nanopost.domain.usecase.GetFeedUseCase
import com.evolitist.nanopost.presentation.extensions.filterData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    getFeedUseCase: GetFeedUseCase,
) : ViewModel() {

    val feedPagingDataFlow = getFeedUseCase()
        .filterData { !it.text.isNullOrBlank() || it.images.isNotEmpty() }
        .cachedIn(viewModelScope)
}
