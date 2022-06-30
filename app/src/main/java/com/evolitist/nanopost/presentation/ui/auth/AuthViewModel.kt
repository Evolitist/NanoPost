package com.evolitist.nanopost.presentation.ui.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.nanopost.domain.model.UsernameCheckResult
import com.evolitist.nanopost.domain.usecase.CheckUsernameUseCase
import com.evolitist.nanopost.domain.usecase.RegisterUseCase
import com.evolitist.nanopost.domain.usecase.SignInUseCase
import com.evolitist.nanopost.domain.usecase.SkipAuthUseCase
import com.evolitist.nanopost.domain.usecase.ValidateUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val validateUsernameUseCase: ValidateUsernameUseCase,
    private val checkUsernameUseCase: CheckUsernameUseCase,
    private val signInUseCase: SignInUseCase,
    private val registerUseCase: RegisterUseCase,
    private val skipAuthUseCase: SkipAuthUseCase,
) : ViewModel() {

    private val _authScreenState = mutableStateOf<AuthScreenState>(AuthScreenState.CheckUsername)
    val authScreenState: State<AuthScreenState> = _authScreenState

    fun authButtonClick(
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            when (_authScreenState.value) {
                AuthScreenState.CheckUsername -> checkUsername(username)
                AuthScreenState.SignIn -> signIn(username, password)
                AuthScreenState.Register -> register(username, password)
                else -> Unit
            }
        }
    }

    fun skipButtonClick() {
        viewModelScope.launch {
            skipAuthUseCase()
        }
    }

    fun clearErrors() {
        _authScreenState.value = _authScreenState.value.success()
    }

    private suspend fun checkUsername(username: String) {
        _authScreenState.value = validateAndCheckUsername(username)
    }

    private suspend fun validateAndCheckUsername(username: String): AuthScreenState {
        return validateUsernameUseCase(username)?.toAuthScreenState() ?: run {
            _authScreenState.setLoading()
            try {
                checkUsernameUseCase(username).toAuthScreenState()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _authScreenState.value.error(e)
            }
        }
    }

    private suspend fun signIn(username: String, password: String) {
        _authScreenState.setLoading()
        try {
            signInUseCase(username, password)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _authScreenState.setError(e)
        }
    }

    private suspend fun register(username: String, password: String) {
        _authScreenState.setLoading()
        try {
            registerUseCase(username, password)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _authScreenState.setError(e)
        }
    }

    private fun MutableState<AuthScreenState>.setLoading() {
        value = value.loading()
    }

    private fun MutableState<AuthScreenState>.setError(error: Any) {
        value = value.error(error)
    }

    private fun UsernameCheckResult.toAuthScreenState() = when (this) {
        UsernameCheckResult.Taken -> AuthScreenState.SignIn
        UsernameCheckResult.Free -> AuthScreenState.Register
        else -> _authScreenState.value.error(this)
    }
}
