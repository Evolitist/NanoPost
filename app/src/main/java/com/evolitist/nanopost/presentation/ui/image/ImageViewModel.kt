package com.evolitist.nanopost.presentation.ui.image

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.nanopost.domain.usecase.DeleteImageUseCase
import com.evolitist.nanopost.domain.usecase.GetImageUseCase
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
class ImageViewModel @Inject constructor(
    private val getImageUseCase: GetImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
) : ViewModel() {

    private val imageIdFlow = MutableStateFlow<String?>(null)

    val imageStateFlow = imageIdFlow
        .filterNotNull()
        .mapLatest { getImageUseCase(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setImageId(imageId: String) {
        imageIdFlow.value = imageId
    }

    fun onDeleteClick(imageId: String, callback: () -> Unit) {
        viewModelScope.launch {
            deleteImageUseCase(imageId)
            callback()
        }
    }
}
