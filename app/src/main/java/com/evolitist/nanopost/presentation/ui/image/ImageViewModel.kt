package com.evolitist.nanopost.presentation.ui.image

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.evolitist.nanopost.domain.usecase.DeleteImageUseCase
import com.evolitist.nanopost.domain.usecase.GetImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class ImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getImageUseCase: GetImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
) : ViewModel() {

    private val imageId = savedStateHandle.toRoute<ImageRoute>().id

    val imageStateFlow = flow {
        emit(getImageUseCase(imageId))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    fun onDeleteClick(callback: () -> Unit) {
        viewModelScope.launch {
            deleteImageUseCase(imageId)
            callback()
        }
    }
}
