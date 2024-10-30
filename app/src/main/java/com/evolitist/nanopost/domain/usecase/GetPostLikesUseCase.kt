package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.PostRepository
import com.evolitist.nanopost.domain.model.Likes
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostLikesUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {

    operator fun invoke(postId: String): Flow<Likes> {
        return postRepository.getPostLikes(postId)
    }
}
