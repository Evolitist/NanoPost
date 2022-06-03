package com.evolitist.nanopost.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiLikes(
    val liked: Boolean,
    val likesCount: Int,
)
