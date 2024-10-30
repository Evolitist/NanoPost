package com.evolitist.nanopost.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiImage(
    val id: String,
    val owner: ApiProfileCompact,
    val dateCreated: Long,
    val contentDescription: String?,
    val blurHash: String?,
    val sizes: List<ApiImageSize>,
)
