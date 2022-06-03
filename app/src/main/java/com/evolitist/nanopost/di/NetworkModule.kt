package com.evolitist.nanopost.di

import com.evolitist.nanopost.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class AuthClient

@Qualifier
annotation class ApiClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJsonConverter() = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideBearerAuthProvider(
        settingsRepository: SettingsRepository,
    ) = BearerAuthProvider(
        realm = null,
        loadTokens = {
            settingsRepository.authToken()?.let { token ->
                BearerTokens(token, "")
            }
        },
        refreshTokens = {
            if (oldTokens != null) {
                oldTokens
            } else {
                val token = settingsRepository.requireAuthToken()
                BearerTokens(token, "")
            }
        },
    ).also {
        settingsRepository.addOnClearListener(it::clearToken)
    }

    @Provides
    @Singleton
    @AuthClient
    fun provideAuthClient(
        json: Json,
    ) = HttpClient(OkHttp) {
        install(Logging) {
            logger = Logger.ANDROID
        }

        install(ContentNegotiation) {
            json(json)
        }
    }

    @Provides
    @Singleton
    @ApiClient
    fun provideApiClient(
        json: Json,
        bearerAuthProvider: BearerAuthProvider,
    ) = HttpClient(OkHttp) {
        install(Logging) {
            logger = Logger.ANDROID
        }

        install(Auth) {
            providers += bearerAuthProvider
        }

        install(ContentNegotiation) {
            json(json)
        }
    }
}
