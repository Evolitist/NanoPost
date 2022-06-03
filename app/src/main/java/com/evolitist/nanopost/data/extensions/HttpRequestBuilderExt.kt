@file:Suppress("NOTHING_TO_INLINE")

package com.evolitist.nanopost.data.extensions

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments

inline fun HttpRequestBuilder.path(vararg args: String?) = url.appendPathSegments(args.filterNotNull())

inline fun HttpRequestBuilder.query(key: String, value: Any?) = parameter(key, value)
