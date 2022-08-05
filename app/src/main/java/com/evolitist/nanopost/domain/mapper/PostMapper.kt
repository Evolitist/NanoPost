package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.data.network.model.ApiPost
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class PostMapper @Inject constructor(
    private val profileCompactMapper: ProfileCompactMapper,
    private val imageMapper: ImageMapper,
    private val likesMapper: LikesMapper,
) {
    fun fromApiToModel(api: ApiPost) = Post(
        id = api.id,
        owner = api.owner.let(profileCompactMapper::fromApiToModel),
        dateCreated = api.dateCreated,
        text = api.text,
        images = api.images.map(imageMapper::fromApiToModel).toImmutableList(),
        likes = api.likes.let(likesMapper::fromApiToModel),
    )
}
