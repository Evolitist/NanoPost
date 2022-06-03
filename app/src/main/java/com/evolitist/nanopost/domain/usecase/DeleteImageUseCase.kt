package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.ImageRepository
import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
) {

    suspend operator fun invoke(imageId: String) {
        return imageRepository.deleteImage(imageId)
    }
}
