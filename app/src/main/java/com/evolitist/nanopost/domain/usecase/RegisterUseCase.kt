package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.AuthRepository
import com.evolitist.nanopost.data.repository.SettingsRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(username: String, password: String) {
        val result = authRepository.register(username, password)
        settingsRepository.userId(result.userId)
        settingsRepository.authToken(result.token)
    }
}
