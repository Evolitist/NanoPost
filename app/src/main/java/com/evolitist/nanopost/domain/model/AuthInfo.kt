package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AuthInfo(
    val userId: String,
    val token: String,
)
