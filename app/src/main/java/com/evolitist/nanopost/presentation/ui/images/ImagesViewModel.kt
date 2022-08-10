package com.evolitist.nanopost.presentation.ui.images

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.evolitist.nanopost.domain.usecase.GetImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@Stable
@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
) : ViewModel() {

    private val profileIdFlow = MutableStateFlow<String?>(null)

    val imagesPagingDataFlow = profileIdFlow
        .flatMapLatest { getImagesUseCase(it) }
        .cachedIn(viewModelScope)

    fun setProfileId(profileId: String?) {
        profileIdFlow.value = profileId
    }
}
