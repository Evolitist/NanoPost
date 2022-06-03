package com.evolitist.nanopost.data.repository

import com.evolitist.nanopost.domain.model.AuthInfo
import com.evolitist.nanopost.domain.model.UsernameCheckResult

interface AuthRepository {

    suspend fun checkUsername(username: String): UsernameCheckResult

    suspend fun register(username: String, password: String): AuthInfo

    suspend fun login(username: String, password: String): AuthInfo
}
