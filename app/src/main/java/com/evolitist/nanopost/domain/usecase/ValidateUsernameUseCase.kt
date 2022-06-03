package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.domain.model.UsernameCheckResult
import com.evolitist.nanopost.domain.validation.UsernameValidator
import javax.inject.Inject

class ValidateUsernameUseCase @Inject constructor(
    private val usernameValidator: UsernameValidator,
) {

    operator fun invoke(username: String): UsernameCheckResult? {
        return usernameValidator.validate(username)
    }
}
