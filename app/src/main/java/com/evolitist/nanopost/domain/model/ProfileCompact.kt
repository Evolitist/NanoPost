package com.evolitist.nanopost.domain.model

data class ProfileCompact(
    override val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val subscribed: Boolean,
) : Identifiable
