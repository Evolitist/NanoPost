package com.evolitist.nanopost.domain.model

import kotlinx.collections.immutable.ImmutableList

data class Post(
    override val id: String,
    val owner: ProfileCompact,
    val dateCreated: Long,
    val text: String?,
    val images: ImmutableList<Image>,
    val likes: Likes,
) : Identifiable
