package com.evolitist.nanopost.data.network

import com.evolitist.nanopost.data.extensions.path
import com.evolitist.nanopost.data.extensions.query
import com.evolitist.nanopost.data.network.model.ApiImage
import com.evolitist.nanopost.data.network.model.ApiPost
import com.evolitist.nanopost.data.network.model.ApiProfile
import com.evolitist.nanopost.data.network.model.Empty
import com.evolitist.nanopost.data.network.model.response.PagedDataResponse
import com.evolitist.nanopost.di.ApiClient
import com.evolitist.nanopost.domain.model.ImageInfo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.escapeIfNeeded
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
        onUpload: (Float) -> Unit,
    ): ApiProfile = put("profile") {
        MultiPartFormDataContent(
            formData {
                append("username", username)
                displayName?.let { append("displayName", it) }
                bio?.let { append("bio", it) }
                avatar?.let {
                    append(
                        key = "avatar",
                        value = it.bytes,
                        headers = Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=${it.fileName.escapeIfNeeded()}"
                            )
                            it.mimeType?.let { mimeType ->
                                append(HttpHeaders.ContentType, mimeType)
                            }
                        },
                    )
                }
            }
        )

        onUpload { bytesSentTotal, contentLength ->
            onUpload(bytesSentTotal.toFloat() / contentLength.toFloat())
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

    suspend fun putImage(
        imageInfo: ImageInfo,
        onUpload: (Float) -> Unit,
    ): ApiImage = put("image") {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append(
                        key = "image",
                        value = imageInfo.bytes,
                        headers = Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=${imageInfo.fileName.escapeIfNeeded()}"
                            )
                            imageInfo.mimeType?.let {
                                append(HttpHeaders.ContentType, it)
                            }
                        },
                    )
                }
            )
        )

        onUpload { bytesSentTotal, contentLength ->
            onUpload(bytesSentTotal.toFloat() / contentLength.toFloat())
        }
    }

    suspend fun getImage(imageId: String): ApiImage = get("image/$imageId")

    suspend fun deleteImage(imageId: String) = delete<Empty>("image/$imageId")

    suspend fun putPost(
        text: String?,
        images: List<ImageInfo>,
        onUpload: (Float) -> Unit,
    ): ApiPost = put("post") {
        setBody(
            MultiPartFormDataContent(
                formData {
                    text?.let { append("text", it) }
                    images.forEachIndexed { index, imageInfo ->
                        append(
                            key = "image${index.inc()}",
                            value = imageInfo.bytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=${imageInfo.fileName.escapeIfNeeded()}")
                                imageInfo.mimeType?.let { append(HttpHeaders.ContentType, it) }
                            },
                        )
                    }
                }
            )
        )

        onUpload { bytesSentTotal, contentLength ->
            onUpload(bytesSentTotal.toFloat() / contentLength.toFloat())
        }
    }

    suspend fun getPost(postId: String) = get<ApiPost>("post/$postId")

    suspend fun deletePost(postId: String) = delete<Empty>("post/$postId")

    suspend fun likePost(postId: String) = put<Empty>("post/$postId/like")

    suspend fun unlikePost(postId: String) = delete<Empty>("post/$postId/like")
}
