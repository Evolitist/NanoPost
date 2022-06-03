package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Likes(
    val liked: Boolean,
    val likesCount: Int,
)
