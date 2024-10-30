package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.domain.model.PasswordCheckResult
import com.evolitist.nanopost.domain.validation.PasswordValidator
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor(
    private val passwordValidator: PasswordValidator,
) {

    operator fun invoke(password: String, confirmPassword: String): PasswordCheckResult? {
        return passwordValidator.validate(password, confirmPassword)
    }
}
