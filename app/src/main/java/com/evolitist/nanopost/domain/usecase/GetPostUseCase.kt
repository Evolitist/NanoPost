package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.PostRepository
import com.evolitist.nanopost.domain.model.Post
import javax.inject.Inject

class GetPostUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {

    suspend operator fun invoke(postId: String): Post {
        return postRepository.getPost(postId)
    }
}
