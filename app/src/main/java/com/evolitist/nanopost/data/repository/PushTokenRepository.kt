package com.evolitist.nanopost.data.repository

interface PushTokenRepository {

    suspend fun putToken(token: String)

    suspend fun deleteToken()
}
