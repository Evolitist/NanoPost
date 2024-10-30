package com.evolitist.nanopost.data.network

import com.evolitist.nanopost.data.extensions.path
import com.evolitist.nanopost.data.extensions.query
import com.evolitist.nanopost.data.network.model.ApiImage
import com.evolitist.nanopost.data.network.model.ApiPost
import com.evolitist.nanopost.data.network.model.ApiProfile
import com.evolitist.nanopost.data.network.model.ApiProfileCompact
import com.evolitist.nanopost.data.network.model.response.PagedDataResponse
import com.evolitist.nanopost.di.ApiClient
import com.evolitist.nanopost.domain.model.ImageInfo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import javax.inject.Inject

class NanoPostApiService @Inject constructor(
    @ApiClient client: HttpClient,
) : BaseApiService(client, "https://nanopost.evolitist.com/api/v1/") {

    suspend fun getFeed(
        count: Int,
        nextFrom: String?,
    ): PagedDataResponse<ApiPost> = get("feed") {
        query("count", count)
        query("offset", nextFrom)
    }

    suspend fun putProfile(
        username: String,
        displayName: String?,
        bio: String?,
        avatar: ImageInfo?,
        onUpload: (Float?) -> Unit,
    ): ApiProfile = put("profile") {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("username", username)
                    displayName?.let { append("displayName", it) }
                    bio?.let { append("bio", it) }
                    avatar?.let {
                        append(
                            key = "avatar",
                            image = it,
                        )
                    }
                }
            )
        )

        onUpload { bytesSentTotal, contentLength ->
            onUpload(contentLength?.let { bytesSentTotal.toFloat() / it.toFloat() })
        }
    }

    suspend fun getProfile(
        id: String?,
    ): ApiProfile = get("profile") {
        path(id)
    }

    suspend fun getImages(
        id: String?,
        count: Int,
        nextFrom: String?,
    ): PagedDataResponse<ApiImage> = get("images") {
        path(id)
        query("count", count)
        query("offset", nextFrom)
    }

    suspend fun getPosts(
        id: String?,
        count: Int,
        nextFrom: String?,
    ): PagedDataResponse<ApiPost> = get("posts") {
        path(id)
        query("count", count)
        query("offset", nextFrom)
    }

    suspend fun getSubscribers(
        id: String?,
        count: Int,
        offset: String?,
    ): PagedDataResponse<ApiProfileCompact> = get("subscribers") {
        path(id)
        query("count", count)
        query("offset", offset)
    }

    suspend fun subscribe(id: String) = put<Unit>("profile/$id/subscribe") {
    }

    suspend fun unsubscribe(id: String) = delete<Unit>("profile/$id/subscribe") {
    }

    suspend fun putImage(
        imageInfo: ImageInfo,
        onUpload: (Float?) -> Unit,
    ): ApiImage = put("image") {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append(
                        key = "image",
                        image = imageInfo,
                    )
                }
            )
        )

        onUpload { bytesSentTotal, contentLength ->
            onUpload(contentLength?.let { bytesSentTotal.toFloat() / it.toFloat() })
        }
    }

    suspend fun getImage(imageId: String): ApiImage = get("image/$imageId")

    suspend fun deleteImage(imageId: String) = delete<Unit>("image/$imageId")

    suspend fun putPost(
        text: String?,
        images: List<ImageInfo>,
        onUpload: (Float?) -> Unit,
    ): ApiPost = put("post") {
        setBody(
            MultiPartFormDataContent(
                formData {
                    text?.let { append("text", it) }
                    append(
                        images = images,
                        keyProducer = { "image${it.inc()}" },
                    )
                }
            )
        )

        onUpload { bytesSentTotal, contentLength ->
            onUpload(contentLength?.let { bytesSentTotal.toFloat() / it.toFloat() })
        }
    }

    suspend fun getPost(postId: String) = get<ApiPost>("post/$postId")

    suspend fun deletePost(postId: String) = delete<Unit>("post/$postId")

    suspend fun likePost(postId: String) = put<ApiPost>("post/$postId/like")

    suspend fun unlikePost(postId: String) = delete<ApiPost>("post/$postId/like")
}
