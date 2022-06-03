package com.evolitist.nanopost.domain.usecase

import android.net.Uri
import com.evolitist.nanopost.data.repository.ContentRepository
import com.evolitist.nanopost.domain.model.ImageInfo
import javax.inject.Inject

class PrepareImageUseCase @Inject constructor(
    private val contentRepository: ContentRepository,
) {

    suspend operator fun invoke(contentUri: Uri): ImageInfo {
        return contentRepository.resolveUri(contentUri)
    }
}
