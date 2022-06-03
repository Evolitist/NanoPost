package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {

    suspend operator fun invoke(postId: String) {
        return postRepository.deletePost(postId)
    }
}
