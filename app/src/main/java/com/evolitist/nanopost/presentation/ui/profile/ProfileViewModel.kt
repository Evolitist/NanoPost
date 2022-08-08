package com.evolitist.nanopost.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.evolitist.nanopost.domain.usecase.GetPostsUseCase
import com.evolitist.nanopost.domain.usecase.GetProfileUseCase
import com.evolitist.nanopost.domain.usecase.SubscribeToProfileUseCase
import com.evolitist.nanopost.domain.usecase.UnsubscribeFromProfileUseCase
import com.evolitist.nanopost.presentation.extensions.mapData
import com.evolitist.nanopost.presentation.extensions.withHeaders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val subscribeToProfileUseCase: SubscribeToProfileUseCase,
    private val unsubscribeFromProfileUseCase: UnsubscribeFromProfileUseCase,
) : ViewModel() {

    private val profileIdFlow = MutableStateFlow<String?>(null)

    private val profileFlow: Flow<List<ProfileElement>>
        get() = profileIdFlow.mapLatest { id ->
            val profile = getProfileUseCase(id)
            listOfNotNull(
                ProfileElement.profile(profile),
                profile.images.takeIf { it.isNotEmpty() }?.let(ProfileElement::images),
            )
        }

    private val profilePagingDataFlow = profileIdFlow
        .flatMapLatest { getPostsUseCase(it) }
        .mapData { ProfileElement.post(it) }
        .withHeaders { profileFlow }
        .cachedIn(viewModelScope)
    val profilePagingItems @Composable get() = profilePagingDataFlow.collectAsLazyPagingItems()

    fun setProfileId(profileId: String?) {
        profileIdFlow.value = profileId
    }

    fun subscribe(callback: () -> Unit) {
        profileIdFlow.value?.let { profileId ->
            viewModelScope.launch {
                subscribeToProfileUseCase(profileId)
                callback()
            }
        }
    }

    fun unsubscribe(callback: () -> Unit) {
        profileIdFlow.value?.let { profileId ->
            viewModelScope.launch {
                unsubscribeFromProfileUseCase(profileId)
                callback()
            }
        }
    }
}
