package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.ImageRepository
import com.evolitist.nanopost.domain.model.Image
import javax.inject.Inject

class GetImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
) {

    suspend operator fun invoke(imageId: String): Image {
        return imageRepository.getImage(imageId)
    }
}
