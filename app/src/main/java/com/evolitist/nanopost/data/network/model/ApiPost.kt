package com.evolitist.nanopost.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiPost(
    val id: String,
    val owner: ApiProfileCompact,
    val dateCreated: Long,
    val text: String?,
    val images: List<ApiImage>,
    val likes: ApiLikes,
)
