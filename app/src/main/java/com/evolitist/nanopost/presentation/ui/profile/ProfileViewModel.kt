package com.evolitist.nanopost.presentation.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.evolitist.nanopost.domain.usecase.GetPostsUseCase
import com.evolitist.nanopost.domain.usecase.GetProfileUseCase
import com.evolitist.nanopost.domain.usecase.SubscribeToProfileUseCase
import com.evolitist.nanopost.domain.usecase.UnsubscribeFromProfileUseCase
import com.evolitist.nanopost.presentation.extensions.filterData
import com.evolitist.nanopost.presentation.extensions.mapValues
import com.evolitist.nanopost.presentation.extensions.withHeaders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProfileUseCase: GetProfileUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val subscribeToProfileUseCase: SubscribeToProfileUseCase,
    private val unsubscribeFromProfileUseCase: UnsubscribeFromProfileUseCase,
) : ViewModel() {

    private val profileId = savedStateHandle.toRoute<ProfileRoute>().id

    private val profileFlow = flow {
        val profile = getProfileUseCase(profileId)
        emit(
            listOfNotNull(
                ProfileElement.profile(profile),
                profile.images.takeIf { it.isNotEmpty() }?.let(ProfileElement::images),
            )
        )
    }

    val profilePagingDataFlow = flow { emitAll(getPostsUseCase(profileId)) }
        .filterData { !it.text.isNullOrBlank() || it.images.isNotEmpty() }
        .mapValues { ProfileElement.post(it) }
        .withHeaders { profileFlow }
        .cachedIn(viewModelScope)

    fun subscribe(callback: () -> Unit) {
        profileId?.let { profileId ->
            viewModelScope.launch {
                subscribeToProfileUseCase(profileId)
                callback()
            }
        }
    }

    fun unsubscribe(callback: () -> Unit) {
        profileId?.let { profileId ->
            viewModelScope.launch {
                unsubscribeFromProfileUseCase(profileId)
                callback()
            }
        }
    }
}
