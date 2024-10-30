package com.evolitist.nanopost.presentation.ui.auth

import com.evolitist.nanopost.domain.model.PasswordCheckResult
import com.evolitist.nanopost.domain.model.UsernameCheckResult

sealed class AuthScreenState(
    val showPasswordField: Boolean,
    val showConfirmPasswordField: Boolean,
) {

    open fun loading(): AuthScreenState = Loading(success())
    open fun success(): AuthScreenState = this
    open fun error(error: Any): AuthScreenState = Error(success(), error)

    sealed class Success(
        showPasswordField: Boolean,
        showConfirmPasswordField: Boolean,
    ) : AuthScreenState(showPasswordField, showConfirmPasswordField) {

        abstract val usernameValidationResult: UsernameCheckResult?
        abstract val passwordValidationResult: PasswordCheckResult?
    }

    data class CheckUsername(
        override val usernameValidationResult: UsernameCheckResult?,
    ) : Success(showPasswordField = false, showConfirmPasswordField = false) {
        override val passwordValidationResult = null
    }

    data class SignIn(
        override val usernameValidationResult: UsernameCheckResult?,
        override val passwordValidationResult: PasswordCheckResult?,
    ) : Success(showPasswordField = true, showConfirmPasswordField = false)

    data class Register(
        override val usernameValidationResult: UsernameCheckResult?,
        override val passwordValidationResult: PasswordCheckResult?,
    ) : Success(showPasswordField = true, showConfirmPasswordField = true)

    data class Loading(
        val previousState: AuthScreenState,
    ) : AuthScreenState(previousState.showPasswordField, previousState.showConfirmPasswordField) {
        override fun loading(): AuthScreenState = this
        override fun success(): AuthScreenState = previousState.success()
        override fun error(error: Any): AuthScreenState = previousState.error(error)
    }

    data class Error(
        val previousState: AuthScreenState,
        val error: Any,
    ) : AuthScreenState(previousState.showPasswordField, previousState.showConfirmPasswordField) {
        override fun loading(): AuthScreenState = previousState.loading()
        override fun success(): AuthScreenState = previousState.success()
        override fun error(error: Any): AuthScreenState = Error(previousState.success(), error)
    }
}
