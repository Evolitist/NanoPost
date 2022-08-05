package com.evolitist.nanopost.domain.model

import kotlinx.collections.immutable.ImmutableList

data class Image(
    override val id: String,
    val owner: ProfileCompact,
    val dateCreated: Long,
    val sizes: ImmutableList<ImageSize>,
) : Identifiable
