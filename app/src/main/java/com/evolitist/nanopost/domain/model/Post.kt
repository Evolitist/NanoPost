package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Post(
    override val id: String,
    val owner: ProfileCompact,
    val dateCreated: Long,
    val text: String?,
    val images: List<Image>,
    val likes: Likes,
) : Identifiable
