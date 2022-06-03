package com.evolitist.nanopost.data.repository

import com.evolitist.nanopost.data.network.AuthApiService
import com.evolitist.nanopost.data.network.model.request.RegisterRequest
import com.evolitist.nanopost.domain.mapper.AuthInfoMapper
import com.evolitist.nanopost.domain.mapper.UsernameCheckResultMapper
import com.evolitist.nanopost.domain.model.AuthInfo
import com.evolitist.nanopost.domain.model.UsernameCheckResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val usernameCheckResultMapper: UsernameCheckResultMapper,
    private val authInfoMapper: AuthInfoMapper,
) : AuthRepository {
    override suspend fun checkUsername(username: String): UsernameCheckResult {
        return apiService.checkUsername(username).result.let(usernameCheckResultMapper::fromApiToModel)
    }

    override suspend fun register(username: String, password: String): AuthInfo {
        return apiService.register(RegisterRequest(username, password)).let(authInfoMapper::fromApiToModel)
    }

    override suspend fun login(username: String, password: String): AuthInfo {
        return apiService.login(username, password).let(authInfoMapper::fromApiToModel)
    }
}
