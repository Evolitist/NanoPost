package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckAppStatusUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    operator fun invoke(): Flow<Boolean> {
        return flow {
            emit(settingsRepository.showAuth())
        }.combine(settingsRepository.authorized()) { showAuth, authorized ->
            showAuth && !authorized
        }
    }
}
