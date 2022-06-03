package com.evolitist.nanopost.data.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val userId: String,
    val token: String,
    val expiresIn: Long? = 0L,
)
