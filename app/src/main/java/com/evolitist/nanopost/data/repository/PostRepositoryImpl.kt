package com.evolitist.nanopost.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.evolitist.nanopost.data.memory.MemoryStorage
import com.evolitist.nanopost.data.network.NanoPostApiService
import com.evolitist.nanopost.data.paging.StringKeyedPagingSource
import com.evolitist.nanopost.domain.mapper.LikesMapper
import com.evolitist.nanopost.domain.mapper.PostMapper
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Likes
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.presentation.extensions.mapValues
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val apiService: NanoPostApiService,
    private val memoryStorage: MemoryStorage,
    private val postMapper: PostMapper,
    private val likesMapper: LikesMapper,
) : PostRepository {

    override fun getFeed(): Flow<PagingData<Post>> {
        return Pager(PagingConfig(30, enablePlaceholders = false)) {
            StringKeyedPagingSource(
                dataLoader = { count, offset -> apiService.getFeed(count, offset) },
                onLoad = { memoryStorage.cacheLikes(it.id, likesMapper.fromApiToModel(it.likes)) },
            )
        }.flow.mapValues(postMapper::fromApiToModel)
    }

    override fun getPosts(profileId: String): Flow<PagingData<Post>> {
        return Pager(PagingConfig(20, enablePlaceholders = false)) {
            StringKeyedPagingSource(
                dataLoader = { count, offset -> apiService.getPosts(profileId, count, offset) },
                onLoad = { memoryStorage.cacheLikes(it.id, likesMapper.fromApiToModel(it.likes)) },
            )
        }.flow.mapValues(postMapper::fromApiToModel)
    }

    override suspend fun putPost(
        text: String?,
        images: List<ImageInfo>,
        progressCallback: (Float?) -> Unit,
    ): Post {
        return apiService.putPost(
            text?.takeIf { it.isNotBlank() },
            images,
            progressCallback,
        ).let(postMapper::fromApiToModel)
            .also { memoryStorage.cacheLikes(it.id, it.likes) }
    }

    override suspend fun getPost(postId: String): Post {
        return apiService.getPost(postId)
            .let(postMapper::fromApiToModel)
            .also { memoryStorage.cacheLikes(it.id, it.likes) }
    }

    override suspend fun deletePost(postId: String) {
        apiService.deletePost(postId)
    }

    override fun getPostLikes(postId: String): Flow<Likes> {
        return memoryStorage.getLikes(postId)
    }

    override suspend fun likePost(postId: String) {
        apiService.likePost(postId)
            .let(postMapper::fromApiToModel)
            .also { memoryStorage.cacheLikes(it.id, it.likes) }
    }

    override suspend fun unlikePost(postId: String) {
        apiService.unlikePost(postId)
            .let(postMapper::fromApiToModel)
            .also { memoryStorage.cacheLikes(it.id, it.likes) }
    }
}
