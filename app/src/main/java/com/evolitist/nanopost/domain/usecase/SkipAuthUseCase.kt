package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.SettingsRepository
import javax.inject.Inject

class SkipAuthUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke() {
        settingsRepository.showAuth(false)
    }
}
