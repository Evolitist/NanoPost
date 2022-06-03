package com.evolitist.nanopost.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put

abstract class BaseApiService(
    protected val client: HttpClient,
    protected val baseUrl: String,
) {
    protected suspend inline fun <reified T> get(endpoint: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        return client.get(baseUrl + endpoint, block).body()
    }

    protected suspend inline fun <reified T> post(endpoint: String, block: HttpRequestBuilder.() -> Unit): T {
        return client.post(baseUrl + endpoint, block).body()
    }

    protected suspend inline fun <reified T> put(endpoint: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        return client.put(baseUrl + endpoint, block).body()
    }

    protected suspend inline fun <reified T> patch(endpoint: String, block: HttpRequestBuilder.() -> Unit): T {
        return client.patch(baseUrl + endpoint, block).body()
    }

    protected suspend inline fun <reified T> delete(endpoint: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        return client.delete(baseUrl + endpoint, block).body()
    }
}
