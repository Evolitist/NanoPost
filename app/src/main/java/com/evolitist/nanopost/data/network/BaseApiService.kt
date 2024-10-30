package com.evolitist.nanopost.data.network

import com.evolitist.nanopost.domain.model.ImageInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.append
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.utils.io.core.writeFully

abstract class BaseApiService(
    protected val client: HttpClient,
    protected val baseUrl: String,
) {
    protected suspend inline fun <reified T> get(endpoint: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        return request(endpoint, HttpMethod.Get, block)
    }

    protected suspend inline fun <reified T> post(endpoint: String, block: HttpRequestBuilder.() -> Unit): T {
        return request(endpoint, HttpMethod.Post, block)
    }

    protected suspend inline fun <reified T> put(endpoint: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        return request(endpoint, HttpMethod.Put, block)
    }

    protected suspend inline fun <reified T> patch(endpoint: String, block: HttpRequestBuilder.() -> Unit): T {
        return request(endpoint, HttpMethod.Patch, block)
    }

    protected suspend inline fun <reified T> delete(endpoint: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        return request(endpoint, HttpMethod.Delete, block)
    }

    protected fun FormBuilder.append(image: ImageInfo, key: String) = append(
        key = key,
        filename = image.fileName,
        contentType = image.mimeType?.let { ContentType.parse(it) },
        size = image.bytes.size.toLong(),
        bodyBuilder = { writeFully(image.bytes) },
    )

    protected fun FormBuilder.append(images: List<ImageInfo>, keyProducer: (Int) -> String) {
        images.forEachIndexed { index, image ->
            append(
                key = keyProducer(index),
                filename = image.fileName,
                contentType = image.mimeType?.let { ContentType.parse(it) },
                size = image.bytes.size.toLong(),
                bodyBuilder = { writeFully(image.bytes) },
            )
        }
    }

    protected suspend inline fun <reified T> request(
        endpoint: String,
        method: HttpMethod,
        block: HttpRequestBuilder.() -> Unit,
    ): T {
        val url = URLBuilder(baseUrl).appendPathSegments(endpoint).build()
        return client.request(url) {
            this.method = method
            block()
        }.body()
    }
}
