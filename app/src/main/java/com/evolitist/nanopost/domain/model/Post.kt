package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class Post(
    override val id: String,
    val owner: ProfileCompact,
    val dateCreated: Long,
    val text: String?,
    val images: ImmutableList<Image>,
    val likes: Likes,
) : Identifiable
