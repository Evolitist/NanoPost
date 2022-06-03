package com.evolitist.nanopost.presentation.extensions

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
data class StableWrapper<T>(val data: T)

@Immutable
data class ImmutableWrapper<T>(val data: T)

fun <T> T.stable() = StableWrapper(this)

fun <T> T.immutable() = ImmutableWrapper(this)
