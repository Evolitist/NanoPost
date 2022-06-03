package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.data.network.model.ApiUsernameCheckResult
import com.evolitist.nanopost.domain.model.UsernameCheckResult
import javax.inject.Inject

class UsernameCheckResultMapper @Inject constructor() {
    fun fromApiToModel(api: ApiUsernameCheckResult) = when (api) {
        ApiUsernameCheckResult.TooShort -> UsernameCheckResult.TooShort
        ApiUsernameCheckResult.TooLong -> UsernameCheckResult.TooLong
        ApiUsernameCheckResult.InvalidCharacters -> UsernameCheckResult.InvalidCharacters
        ApiUsernameCheckResult.Taken -> UsernameCheckResult.Taken
        ApiUsernameCheckResult.Free -> UsernameCheckResult.Free
    }
}
