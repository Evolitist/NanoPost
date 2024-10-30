package com.evolitist.nanopost.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiProfile(
    val id: String,
    val username: String,
    val displayName: String?,
    val bio: String?,
    val avatarId: String?,
    val avatarLarge: String?,
    val avatarSmall: String?,
    val avatarBlurHash: String?,
    val subscribed: Boolean,
    val subscribersCount: Int,
    val postsCount: Int,
    val imagesCount: Int,
    val images: List<ApiImage>,
)
