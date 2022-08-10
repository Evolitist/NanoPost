package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ImageSize(
    val width: Int,
    val height: Int,
    val url: String,
)
