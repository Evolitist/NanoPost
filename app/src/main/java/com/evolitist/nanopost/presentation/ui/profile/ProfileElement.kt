package com.evolitist.nanopost.presentation.ui.profile

import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.domain.model.Profile

sealed class ProfileElement(val type: String, val id: String? = null) {

    companion object {
        fun profile(profile: Profile): ProfileElement = ProfileItem(profile)
        fun images(images: List<Image>): ProfileElement = ImagesItem(images)
        fun post(post: Post): ProfileElement = PostItem(post)
    }

    data class ProfileItem(
        val profile: Profile,
    ) : ProfileElement("profile")

    data class ImagesItem(
        val images: List<Image>,
    ) : ProfileElement("images")

    data class PostItem(
        val post: Post,
    ) : ProfileElement("post", "post_${post.id}")
}
