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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
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

    private val usernameStateFlow = MutableStateFlow("")
    private val passwordStateFlow = MutableStateFlow("")
    private val authButtonEventFlow = MutableStateFlow(Any())

    val authStateFlow = authButtonEventFlow.drop(1)
        .distinctUntilChanged()
        .transform {
            usernameStateFlow.value.takeIf { it.isNotBlank() }?.let { username ->
                validateUsernameUseCase(username)?.let {
                    emit(it)
                } ?: run {
                    emit(AuthScreenState.CheckUsername.loading())
                    emit(checkUsernameUseCase(username).toAuthScreenState())
                }
            }
        }

    private val _usernameValidation = mutableStateOf<UsernameCheckResult?>(null)
    val usernameValidation: State<UsernameCheckResult?> = _usernameValidation

    private val _authScreenState = mutableStateOf<AuthScreenState>(AuthScreenState.CheckUsername)
    val authScreenState: State<AuthScreenState> = _authScreenState

    fun authButtonClick(
        username: String,
        password: String,
        callback: (() -> Unit)? = null,
    ) = viewModelScope.launch {
        when (authScreenState.value) {
            AuthScreenState.CheckUsername -> checkUsername(username)
            AuthScreenState.SignIn -> signIn(username, password)
            AuthScreenState.Register -> register(username, password)
            else -> Unit
        }
        callback?.invoke()
    }

    fun skipButtonClick() {
        viewModelScope.launch {
            skipAuthUseCase()
        }
    }

    private suspend fun checkUsername(username: String) {
        val validationResult = validateUsernameUseCase(username)
        _usernameValidation.value = validationResult
        if (validationResult != null) {
            return
        }
        _authScreenState.value = _authScreenState.value.loading()
        _authScreenState.value = when (checkUsernameUseCase(username)) {
            UsernameCheckResult.TooShort -> {
                _usernameValidation.value = UsernameCheckResult.TooShort
                _authScreenState.value.notLoading()
            }
            UsernameCheckResult.TooLong -> {
                _usernameValidation.value = UsernameCheckResult.TooLong
                _authScreenState.value.notLoading()
            }
            UsernameCheckResult.InvalidCharacters -> {
                _usernameValidation.value = UsernameCheckResult.InvalidCharacters
                _authScreenState.value.notLoading()
            }
            UsernameCheckResult.Taken -> AuthScreenState.SignIn
            UsernameCheckResult.Free -> AuthScreenState.Register
        }
    }

    private suspend fun signIn(username: String, password: String) {
        _authScreenState.setLoading(true)
        signInUseCase(username, password)
    }

    private suspend fun register(username: String, password: String) {
        _authScreenState.setLoading(true)
        registerUseCase(username, password)
    }

    private fun MutableState<AuthScreenState>.setLoading(loading: Boolean) {
        value = value.run { if (loading) value.loading() else value.notLoading() }
    }

    private fun UsernameCheckResult.toAuthScreenState() = when (this) {
        UsernameCheckResult.Taken -> AuthScreenState.SignIn
        UsernameCheckResult.Free -> AuthScreenState.Register
        else -> AuthScreenState.CheckUsername.error(this)
    }
}
