package com.evolitist.nanopost.presentation.ui.images

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.evolitist.nanopost.domain.usecase.GetImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getImagesUseCase: GetImagesUseCase,
) : ViewModel() {

    private val profileId = savedStateHandle.toRoute<ImagesRoute>().id

    val imagesPagingDataFlow = flow {
        emitAll(getImagesUseCase(profileId))
    }.cachedIn(viewModelScope)
}
