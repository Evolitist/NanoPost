package com.evolitist.nanopost.presentation.ui.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolitist.nanopost.domain.model.PasswordCheckResult
import com.evolitist.nanopost.domain.model.UsernameCheckResult
import com.evolitist.nanopost.domain.usecase.CheckUsernameUseCase
import com.evolitist.nanopost.domain.usecase.RegisterUseCase
import com.evolitist.nanopost.domain.usecase.SignInUseCase
import com.evolitist.nanopost.domain.usecase.SkipAuthUseCase
import com.evolitist.nanopost.domain.usecase.ValidatePasswordUseCase
import com.evolitist.nanopost.domain.usecase.ValidateUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val validateUsernameUseCase: ValidateUsernameUseCase,
    private val checkUsernameUseCase: CheckUsernameUseCase,
    private val signInUseCase: SignInUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val registerUseCase: RegisterUseCase,
    private val skipAuthUseCase: SkipAuthUseCase,
) : ViewModel() {

    private val _authScreenState = mutableStateOf<AuthScreenState>(AuthScreenState.CheckUsername(null))
    val authScreenState: State<AuthScreenState> = _authScreenState

    fun authButtonClick(
        username: String,
        password: String,
        confirmPassword: String,
    ) {
        viewModelScope.launch {
            when (_authScreenState.value) {
                is AuthScreenState.CheckUsername -> checkUsername(username)
                is AuthScreenState.SignIn -> signIn(username, password)
                is AuthScreenState.Register -> register(username, password, confirmPassword)
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
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                _authScreenState.value.error(e)
            }
        }
    }

    private suspend fun signIn(username: String, password: String) {
        _authScreenState.setLoading()
        try {
            signInUseCase(username, password)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            _authScreenState.setError(e)
        }
    }

    private suspend fun register(
        username: String,
        password: String,
        confirmPassword: String,
    ) {
        val newState = validatePasswordUseCase(password, confirmPassword)?.toAuthScreenState()
        newState?.let { _authScreenState.value = it } ?: run {
            _authScreenState.setLoading()
            try {
                registerUseCase(username, password)
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                _authScreenState.setError(e)
            }
        }
    }

    private fun MutableState<AuthScreenState>.setLoading() {
        value = value.loading()
    }

    private fun MutableState<AuthScreenState>.setError(error: Any) {
        value = value.error(error)
    }

    private fun UsernameCheckResult.toAuthScreenState(): AuthScreenState = when (this) {
        UsernameCheckResult.Taken -> AuthScreenState.SignIn(null, null)
        UsernameCheckResult.Free -> AuthScreenState.Register(null, null)
        else -> AuthScreenState.CheckUsername(this)
    }

    private fun PasswordCheckResult.toAuthScreenState(): AuthScreenState = when (this) {
        PasswordCheckResult.Valid -> AuthScreenState.Register(null, null)
        else -> AuthScreenState.Register(null, this)
    }
}
