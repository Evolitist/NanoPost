package com.evolitist.nanopost.data.repository

import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Profile

interface ProfileRepository {

    suspend fun getProfile(id: String): Profile

    suspend fun putProfile(
        username: String,
        displayName: String?,
        bio: String?,
        avatar: ImageInfo?,
        progressCallback: (Float) -> Unit,
    ): Profile
}
