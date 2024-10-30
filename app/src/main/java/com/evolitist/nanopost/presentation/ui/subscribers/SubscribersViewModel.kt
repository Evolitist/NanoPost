package com.evolitist.nanopost.presentation.ui.subscribers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.evolitist.nanopost.domain.usecase.GetSubscribersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class SubscribersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getSubscribersUseCase: GetSubscribersUseCase,
) : ViewModel() {

    private val profileId = savedStateHandle.toRoute<SubscribersRoute>().id

    val subscribersPagingDataFlow = flow { emitAll(getSubscribersUseCase(profileId)) }
        .cachedIn(viewModelScope)
}
