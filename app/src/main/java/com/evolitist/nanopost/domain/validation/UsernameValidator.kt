package com.evolitist.nanopost.domain.validation

import com.evolitist.nanopost.domain.model.UsernameCheckResult
import javax.inject.Inject

class UsernameValidator @Inject constructor() {

    companion object {
        private const val UsernameMinLength = 3
        private const val UsernameMaxLength = 16
        private val UsernameRegex = Regex("""[a-z_.]+""")
    }

    fun validate(value: String): UsernameCheckResult? {
        return if (value.length < UsernameMinLength) {
            UsernameCheckResult.TooShort
        } else if (value.length > UsernameMaxLength) {
            UsernameCheckResult.TooLong
        } else if (!UsernameRegex.matches(value)) {
            UsernameCheckResult.InvalidCharacters
        } else {
            null
        }
    }
}
