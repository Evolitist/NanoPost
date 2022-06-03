package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.PostRepository
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Post
import javax.inject.Inject

class UploadPostUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {

    suspend operator fun invoke(
        text: String?,
        images: List<ImageInfo>,
        progressCallback: (Float) -> Unit = {},
    ): Post {
        return postRepository.putPost(
            text,
            images,
            progressCallback,
        )
    }
}
