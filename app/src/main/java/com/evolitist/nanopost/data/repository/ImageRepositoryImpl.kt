package com.evolitist.nanopost.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.evolitist.nanopost.data.network.NanoPostApiService
import com.evolitist.nanopost.data.paging.StringKeyedPagingSource
import com.evolitist.nanopost.domain.mapper.ImageMapper
import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.presentation.extensions.mapData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val apiService: NanoPostApiService,
    private val imageMapper: ImageMapper,
) : ImageRepository {

    override suspend fun putImage(
        imageInfo: ImageInfo,
        progressCallback: (Float) -> Unit,
    ): Image {
        return apiService.putImage(
            imageInfo,
            progressCallback,
        ).let(imageMapper::fromApiToModel)
    }

    override suspend fun getImage(imageId: String): Image {
        return apiService.getImage(imageId).let(imageMapper::fromApiToModel)
    }

    override suspend fun deleteImage(imageId: String) {
        apiService.deleteImage(imageId)
    }

    override fun getImages(profileId: String): Flow<PagingData<Image>> {
        return Pager(PagingConfig(30, enablePlaceholders = false)) {
            StringKeyedPagingSource { count, nextFrom ->
                apiService.getImages(profileId, count, nextFrom)
            }
        }.flow.mapData(imageMapper::fromApiToModel)
    }
}
