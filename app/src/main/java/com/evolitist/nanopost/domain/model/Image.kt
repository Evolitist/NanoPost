package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class Image(
    override val id: String,
    val owner: ProfileCompact,
    val dateCreated: Long,
    val sizes: ImmutableList<ImageSize>,
) : Identifiable
