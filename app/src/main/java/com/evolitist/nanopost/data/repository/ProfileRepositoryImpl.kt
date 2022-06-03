package com.evolitist.nanopost.data.repository

import com.evolitist.nanopost.data.network.NanoPostApiService
import com.evolitist.nanopost.domain.mapper.ProfileMapper
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Profile
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: NanoPostApiService,
    private val profileMapper: ProfileMapper,
) : ProfileRepository {

    override suspend fun getProfile(id: String): Profile {
        return apiService.getProfile(id).let(profileMapper::fromApiToModel)
    }

    override suspend fun putProfile(
        username: String,
        displayName: String?,
        bio: String?,
        avatar: ImageInfo?,
        progressCallback: (Float) -> Unit
    ): Profile {
        return apiService.putProfile(
            username = username,
            displayName = displayName,
            bio = bio,
            avatar = avatar,
            onUpload = progressCallback,
        ).let(profileMapper::fromApiToModel)
    }
}
