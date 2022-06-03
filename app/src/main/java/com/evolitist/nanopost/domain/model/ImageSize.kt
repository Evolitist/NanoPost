package com.evolitist.nanopost.domain.model

import javax.annotation.concurrent.Immutable

@Immutable
data class ImageSize(
    val width: Int,
    val height: Int,
    val url: String,
)
