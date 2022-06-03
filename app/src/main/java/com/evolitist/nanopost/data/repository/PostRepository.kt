package com.evolitist.nanopost.data.repository

import androidx.paging.PagingData
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    fun getFeed(): Flow<PagingData<Post>>

    fun getPosts(profileId: String): Flow<PagingData<Post>>

    suspend fun putPost(
        text: String?,
        images: List<ImageInfo>,
        progressCallback: (Float) -> Unit,
    ): Post

    suspend fun getPost(postId: String): Post

    suspend fun deletePost(postId: String)

    suspend fun likePost(postId: String)

    suspend fun unlikePost(postId: String)
}
