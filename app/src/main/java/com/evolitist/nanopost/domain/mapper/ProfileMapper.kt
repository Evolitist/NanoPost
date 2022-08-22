package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.data.network.model.ApiProfile
import com.evolitist.nanopost.domain.model.Profile
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class ProfileMapper @Inject constructor(
    private val imageMapper: ImageMapper,
) {
    fun fromApiToModel(api: ApiProfile) = Profile(
        id = api.id,
        username = api.username,
        displayName = api.displayName,
        bio = api.bio,
        avatarId = api.avatarId,
        avatarLarge = api.avatarLarge,
        avatarSmall = api.avatarSmall,
        subscribed = api.subscribed,
        subscribersCount = api.subscribersCount,
        postsCount = api.postsCount,
        imagesCount = api.imagesCount,
        images = api.images.map(imageMapper::fromApiToModel).toImmutableList(),
    )
}
