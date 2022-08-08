package com.evolitist.nanopost.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun authToken(): String?
    suspend fun authToken(value: String?)
    suspend fun requireAuthToken(): String

    suspend fun userId(): String?
    suspend fun userId(value: String?)

    suspend fun showAuth(value: Boolean)

    fun authorized(): Flow<Boolean>
    fun showAuth(): Flow<Boolean>

    suspend fun clear()
    fun addOnClearListener(listener: () -> Unit)
    fun removeOnClearListener(listener: () -> Unit)
}
