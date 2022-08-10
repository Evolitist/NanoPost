package com.evolitist.nanopost.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileCompact(
    override val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val subscribed: Boolean,
) : Identifiable
