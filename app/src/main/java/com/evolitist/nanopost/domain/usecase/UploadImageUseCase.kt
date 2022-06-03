package com.evolitist.nanopost.domain.usecase

import android.net.Uri
import com.evolitist.nanopost.data.repository.ImageRepository
import com.evolitist.nanopost.domain.model.Image
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val prepareImageUseCase: PrepareImageUseCase,
) {

    suspend operator fun invoke(
        contentUri: Uri,
        progressCallback: (Float) -> Unit,
    ): Image {
        return imageRepository.putImage(
            prepareImageUseCase(contentUri),
            progressCallback,
        )
    }
}
