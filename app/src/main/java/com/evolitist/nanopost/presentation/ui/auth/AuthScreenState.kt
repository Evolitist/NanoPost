package com.evolitist.nanopost.presentation.ui.auth

sealed class AuthScreenState(val showPasswordField: Boolean) {

    object CheckUsername : AuthScreenState(false)
    object SignIn : AuthScreenState(true)
    object Register : AuthScreenState(true)

    data class Loading(
        val previousState: AuthScreenState,
    ) : AuthScreenState(previousState.showPasswordField) {
        override fun loading(): AuthScreenState = this
        override fun notLoading(): AuthScreenState = previousState
        override fun <T> error(error: T): AuthScreenState = previousState.error(error)
    }

    data class Error<T>(
        val previousState: AuthScreenState,
        val error: T,
    ) : AuthScreenState(previousState.showPasswordField) {

        override val isError = true

        override fun loading(): AuthScreenState = previousState.loading()
        override fun notLoading(): AuthScreenState = previousState.notLoading()
        override fun <T> error(error: T): AuthScreenState = Error(previousState, error)
    }

    open val isError = false

    open fun loading(): AuthScreenState = Loading(this)

    open fun notLoading(): AuthScreenState = this

    open fun <T> error(error: T): AuthScreenState = Error(this, error)
}
