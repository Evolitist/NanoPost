package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Image(
    override val id: String,
    val owner: ProfileCompact,
    val dateCreated: Long,
    val sizes: List<ImageSize>,
) : Identifiable
