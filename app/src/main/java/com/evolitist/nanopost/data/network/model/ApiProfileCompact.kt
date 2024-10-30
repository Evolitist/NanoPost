package com.evolitist.nanopost.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiProfileCompact(
    val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val avatarBlurHash: String?,
    val subscribed: Boolean = false,
)
