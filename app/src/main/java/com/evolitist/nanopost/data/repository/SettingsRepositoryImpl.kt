package com.evolitist.nanopost.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private val onClearListeners = mutableSetOf<() -> Unit>()

    override suspend fun authToken() = dataStore.get(authTokenKey)
    override suspend fun authToken(value: String?) = dataStore.set(authTokenKey, value)
    override suspend fun requireAuthToken() = dataStore.flow(authTokenKey).filterNotNull().first()

    override suspend fun userId() = dataStore.get(userIdKey)
    override suspend fun userId(value: String?) = dataStore.set(userIdKey, value)

    override suspend fun showAuth() = dataStore.get(showAuth) ?: true
    override suspend fun showAuth(value: Boolean) = dataStore.set(showAuth, value)

    override fun authorized() = dataStore.data
        .map { it[authTokenKey] != null }
        .distinctUntilChanged()

    override suspend fun clear() {
        dataStore.edit { it.clear() }
        onClearListeners.forEach { it.invoke() }
    }

    override fun addOnClearListener(listener: () -> Unit) {
        onClearListeners += listener
    }

    override fun removeOnClearListener(listener: () -> Unit) {
        onClearListeners -= listener
    }

    companion object {
        private val authTokenKey = stringPreferencesKey("authToken")
        private val userIdKey = stringPreferencesKey("userId")
        private val showAuth = booleanPreferencesKey("showAuth")

        private suspend fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>) = data.first()[key]
        private suspend fun <T> DataStore<Preferences>.set(key: Preferences.Key<T>, value: T?) {
            edit { settings ->
                if (value != null) {
                    settings[key] = value
                } else {
                    settings.remove(key)
                }
            }
        }
        private fun <T> DataStore<Preferences>.flow(key: Preferences.Key<T>) = data.map { it[key] }
    }
}
