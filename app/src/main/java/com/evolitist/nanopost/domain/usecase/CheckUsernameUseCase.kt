package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.domain.model.UsernameCheckResult
import com.evolitist.nanopost.data.repository.AuthRepository
import javax.inject.Inject

class CheckUsernameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(username: String): UsernameCheckResult {
        return authRepository.checkUsername(username)
    }
}
