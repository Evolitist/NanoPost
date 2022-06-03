package com.evolitist.nanopost.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiImageSize(
    val width: Int,
    val height: Int,
    val url: String,
)
