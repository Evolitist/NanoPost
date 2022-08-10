package com.evolitist.nanopost.presentation.ui.profile

import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.domain.model.Profile
import kotlinx.collections.immutable.ImmutableList
import javax.annotation.concurrent.Immutable

sealed class ProfileElement(val type: String, val id: String? = null) {

    companion object {
        fun profile(profile: Profile): ProfileElement = ProfileItem(profile)
        fun images(images: ImmutableList<Image>): ProfileElement = ImagesItem(images)
        fun post(post: Post): ProfileElement = PostItem(post)
    }

    @Immutable
    data class ProfileItem(
        val profile: Profile,
    ) : ProfileElement("profile")

    @Immutable
    data class ImagesItem(
        val images: ImmutableList<Image>,
    ) : ProfileElement("images")

    @Immutable
    data class PostItem(
        val post: Post,
    ) : ProfileElement("post", "post_${post.id}")
}
