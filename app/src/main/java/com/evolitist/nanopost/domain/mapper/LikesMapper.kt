package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.data.network.model.ApiLikes
import com.evolitist.nanopost.domain.model.Likes
import javax.inject.Inject

class LikesMapper @Inject constructor() {
    fun fromApiToModel(api: ApiLikes) = Likes(
        liked = api.liked,
        likesCount = api.likesCount,
    )
}
