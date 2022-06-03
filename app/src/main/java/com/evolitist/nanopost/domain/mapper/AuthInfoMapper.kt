package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.data.network.model.response.TokenResponse
import com.evolitist.nanopost.domain.model.AuthInfo
import javax.inject.Inject

class AuthInfoMapper @Inject constructor() {

    fun fromApiToModel(api: TokenResponse) = AuthInfo(
        userId = api.userId,
        token = api.token,
    )
}
