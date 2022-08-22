package com.evolitist.nanopost.data.network

import com.evolitist.nanopost.data.extensions.query
import com.evolitist.nanopost.data.network.model.ApiUsernameCheckResult
import com.evolitist.nanopost.data.network.model.request.RegisterRequest
import com.evolitist.nanopost.data.network.model.response.ObjectResponse
import com.evolitist.nanopost.data.network.model.response.TokenResponse
import com.evolitist.nanopost.di.AuthClient
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthApiService @Inject constructor(
    @AuthClient client: HttpClient,
) : BaseApiService(client, "https://nanopost.evolitist.com/api/auth/") {

    suspend fun register(request: RegisterRequest): TokenResponse = post("register") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    suspend fun login(
        username: String,
        password: String,
    ): TokenResponse = get("login") {
        query("username", username)
        query("password", password)
    }

    suspend fun checkUsername(username: String): ObjectResponse<ApiUsernameCheckResult> = get("checkUsername") {
        query("username", username)
    }
}
