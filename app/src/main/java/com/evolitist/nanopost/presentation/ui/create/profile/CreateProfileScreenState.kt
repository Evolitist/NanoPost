package com.evolitist.nanopost.presentation.ui.create.profile

sealed class CreateProfileScreenState(val page: Int) {

    object CheckUsername : CreateProfileScreenState(0)
    object FillData : CreateProfileScreenState(1)

    data class Loading(
        val previousState: CreateProfileScreenState,
    ) : CreateProfileScreenState(previousState.page) {
        override fun loading(): CreateProfileScreenState = this
        override fun notLoading(): CreateProfileScreenState = previousState
        override fun <T> error(error: T): CreateProfileScreenState = previousState.error(error)
    }

    data class Error<T>(
        val previousState: CreateProfileScreenState,
        val error: T,
    ) : CreateProfileScreenState(previousState.page) {
        override fun loading(): CreateProfileScreenState = previousState.loading()
        override fun notLoading(): CreateProfileScreenState = previousState.notLoading()
        override fun <T> error(error: T): CreateProfileScreenState = Error(previousState, error)
    }

    open fun loading(): CreateProfileScreenState = Loading(this)

    open fun notLoading(): CreateProfileScreenState = this

    open fun <T> error(error: T): CreateProfileScreenState = Error(this, error)
}
