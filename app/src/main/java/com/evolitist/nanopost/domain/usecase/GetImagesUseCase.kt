package com.evolitist.nanopost.domain.usecase

import androidx.paging.PagingData
import com.evolitist.nanopost.data.repository.ImageRepository
import com.evolitist.nanopost.domain.model.Image
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val getLocalUserIdUseCase: GetLocalUserIdUseCase,
) {

    suspend operator fun invoke(profileId: String?): Flow<PagingData<Image>> {
        return imageRepository.getImages(
            profileId ?: getLocalUserIdUseCase()!!,
        )
    }
}
