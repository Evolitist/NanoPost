package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckIsAuthorizedUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    operator fun invoke(): Flow<Boolean> {
        return settingsRepository.authorized()
    }
}
