package com.evolitist.nanopost.data.repository

import androidx.paging.PagingData
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Profile
import com.evolitist.nanopost.domain.model.ProfileCompact
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getProfile(id: String): Profile

    suspend fun putProfile(
        username: String,
        displayName: String?,
        bio: String?,
        avatar: ImageInfo?,
        progressCallback: (Float) -> Unit,
    ): Profile

    fun getSubscribers(profileId: String): Flow<PagingData<ProfileCompact>>

    suspend fun subscribe(profileId: String)

    suspend fun unsubscribe(profileId: String)
}
