package com.evolitist.nanopost.presentation.ui.subscribers

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.evolitist.nanopost.domain.usecase.GetSubscribersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SubscribersViewModel @Inject constructor(
    getSubscribersUseCase: GetSubscribersUseCase,
) : ViewModel() {

    private val profileIdFlow = MutableStateFlow<String?>(null)

    private val subscribersPagingDataFlow = profileIdFlow
        .flatMapLatest { getSubscribersUseCase(it) }
        .cachedIn(viewModelScope)
    val subscribersPagingItems @Composable get() = subscribersPagingDataFlow.collectAsLazyPagingItems()

    fun setProfileId(profileId: String?) {
        profileIdFlow.value = profileId
    }
}
