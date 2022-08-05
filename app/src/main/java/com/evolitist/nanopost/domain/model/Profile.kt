package com.evolitist.nanopost.domain.model

import kotlinx.collections.immutable.ImmutableList

data class Profile(
    override val id: String,
    val username: String,
    val displayName: String?,
    val bio: String?,
    val avatarId: String?,
    val avatarLarge: String?,
    val avatarSmall: String?,
    val subscribed: Boolean,
    val subscribersCount: Int,
    val postsCount: Int,
    val imagesCount: Int,
    val images: ImmutableList<Image>,
) : Identifiable {

    fun compact() = ProfileCompact(
        id = id,
        username = username,
        displayName = displayName,
        avatarUrl = avatarSmall,
        subscribed = subscribed,
    )
}
