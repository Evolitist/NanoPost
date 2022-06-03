package com.evolitist.nanopost.data.repository

import androidx.paging.PagingData
import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.domain.model.ImageInfo
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    suspend fun putImage(
        imageInfo: ImageInfo,
        progressCallback: (Float) -> Unit,
    ): Image

    suspend fun getImage(imageId: String): Image

    suspend fun deleteImage(imageId: String)

    fun getImages(profileId: String): Flow<PagingData<Image>>
}
