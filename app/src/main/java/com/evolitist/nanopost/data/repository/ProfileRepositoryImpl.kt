package com.evolitist.nanopost.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.evolitist.nanopost.data.network.NanoPostApiService
import com.evolitist.nanopost.data.paging.StringKeyedPagingSource
import com.evolitist.nanopost.domain.mapper.ProfileCompactMapper
import com.evolitist.nanopost.domain.mapper.ProfileMapper
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Profile
import com.evolitist.nanopost.domain.model.ProfileCompact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: NanoPostApiService,
    private val profileMapper: ProfileMapper,
    private val profileCompactMapper: ProfileCompactMapper,
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

    override fun getSubscribers(profileId: String): Flow<PagingData<ProfileCompact>> {
        return Pager(PagingConfig(50, enablePlaceholders = false)) {
            StringKeyedPagingSource { count, offset ->
                apiService.getSubscribers(profileId, count, offset)
            }
        }.flow.map {
            it.map(profileCompactMapper::fromApiToModel)
        }
    }

    override suspend fun subscribe(profileId: String) {
        apiService.subscribe(profileId)
    }

    override suspend fun unsubscribe(profileId: String) {
        apiService.unsubscribe(profileId)
    }
}
