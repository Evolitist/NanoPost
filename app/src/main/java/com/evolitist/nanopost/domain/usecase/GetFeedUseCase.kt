package com.evolitist.nanopost.domain.usecase

import androidx.paging.PagingData
import com.evolitist.nanopost.data.repository.PostRepository
import com.evolitist.nanopost.domain.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {

    operator fun invoke(): Flow<PagingData<Post>> {
        return postRepository.getFeed()
    }
}
