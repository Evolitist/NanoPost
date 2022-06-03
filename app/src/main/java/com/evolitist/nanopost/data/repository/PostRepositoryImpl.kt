package com.evolitist.nanopost.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.evolitist.nanopost.data.network.NanoPostApiService
import com.evolitist.nanopost.domain.mapper.PostMapper
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.data.paging.StringKeyedPagingSource
import com.evolitist.nanopost.domain.model.ImageInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val apiService: NanoPostApiService,
    private val postMapper: PostMapper,
) : PostRepository {

    override fun getFeed(): Flow<PagingData<Post>> {
        return Pager(PagingConfig(30, enablePlaceholders = false)) {
            StringKeyedPagingSource { count, offset ->
                apiService.getFeed(count, offset)
            }
        }.flow.map {
            it.map(postMapper::fromApiToModel)
        }
    }

    override fun getPosts(profileId: String): Flow<PagingData<Post>> {
        return Pager(PagingConfig(2, enablePlaceholders = false)) {
            StringKeyedPagingSource { count, offset ->
                apiService.getPosts(profileId, count, offset)
            }
        }.flow.map {
            it.map(postMapper::fromApiToModel)
        }
    }

    override suspend fun putPost(
        text: String?,
        images: List<ImageInfo>,
        progressCallback: (Float) -> Unit,
    ): Post {
        return apiService.putPost(
            text?.takeIf { it.isNotBlank() },
            images,
            progressCallback,
        ).let(postMapper::fromApiToModel)
    }

    override suspend fun getPost(postId: String): Post {
        return apiService.getPost(postId).let(postMapper::fromApiToModel)
    }

    override suspend fun deletePost(postId: String) {
        apiService.deletePost(postId)
    }

    override suspend fun likePost(postId: String) {
        apiService.likePost(postId)
    }

    override suspend fun unlikePost(postId: String) {
        apiService.unlikePost(postId)
    }
}
