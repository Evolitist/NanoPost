package com.evolitist.nanopost.presentation.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.nanopost.domain.usecase.CheckIsAuthorizedUseCase
import com.evolitist.nanopost.domain.usecase.CheckShowAuthUseCase
import com.evolitist.nanopost.domain.usecase.GetProfileUseCase
import com.evolitist.nanopost.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class MainViewModel @Inject constructor(
    checkShowAuthUseCase: CheckShowAuthUseCase,
    checkIsAuthorizedUseCase: CheckIsAuthorizedUseCase,
    getProfileUseCase: GetProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val authorizedFlow = checkIsAuthorizedUseCase()
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val showAuthFlow = checkShowAuthUseCase()
        .combine(authorizedFlow) { showAuth, authorized -> showAuth && !authorized }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val profileFlow = authorizedFlow
        .mapLatest { authorized ->
            if (authorized) getProfileUseCase(null).compact() else null
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
