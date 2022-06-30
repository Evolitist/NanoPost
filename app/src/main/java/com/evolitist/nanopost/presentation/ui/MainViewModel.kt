package com.evolitist.nanopost.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.nanopost.domain.usecase.CheckAppStatusUseCase
import com.evolitist.nanopost.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    checkAppStatusUseCase: CheckAppStatusUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    val appStatusFlow = checkAppStatusUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
