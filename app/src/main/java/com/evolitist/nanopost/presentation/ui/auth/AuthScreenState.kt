package com.evolitist.nanopost.presentation.ui.auth

sealed class AuthScreenState(val showPasswordField: Boolean) {

    open fun loading(): AuthScreenState = Loading(success())
    open fun success(): AuthScreenState = this
    open fun error(error: Any): AuthScreenState = Error(success(), error)

    object CheckUsername : AuthScreenState(false)
    object SignIn : AuthScreenState(true)
    object Register : AuthScreenState(true)

    data class Loading(
        val previousState: AuthScreenState,
    ) : AuthScreenState(previousState.showPasswordField) {
        override fun loading(): AuthScreenState = this
        override fun success(): AuthScreenState = previousState.success()
        override fun error(error: Any): AuthScreenState = previousState.error(error)
    }

    data class Error(
        val previousState: AuthScreenState,
        val error: Any,
    ) : AuthScreenState(previousState.showPasswordField) {
        override fun loading(): AuthScreenState = previousState.loading()
        override fun success(): AuthScreenState = previousState.success()
        override fun error(error: Any): AuthScreenState = Error(previousState.success(), error)
    }
}
