package com.evolitist.nanopost.domain.usecase

import androidx.paging.PagingData
import com.evolitist.nanopost.data.repository.PostRepository
import com.evolitist.nanopost.domain.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val getLocalUserIdUseCase: GetLocalUserIdUseCase,
) {

    suspend operator fun invoke(profileId: String?): Flow<PagingData<Post>> {
        return postRepository.getPosts(
            profileId ?: getLocalUserIdUseCase()!!,
        )
    }
}
