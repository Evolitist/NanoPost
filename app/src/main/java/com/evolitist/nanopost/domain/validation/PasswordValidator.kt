package com.evolitist.nanopost.domain.validation

import com.evolitist.nanopost.domain.model.PasswordCheckResult
import javax.inject.Inject

class PasswordValidator @Inject constructor() {

    companion object {
        private const val PasswordMinLength = 8
    }

    fun validate(value: String, confirmValue: String): PasswordCheckResult? {
        return if (value.length < PasswordMinLength) {
            PasswordCheckResult.TooShort
        } else if (value != confirmValue) {
            PasswordCheckResult.NotEqual
        } else {
            null
        }
    }
}
