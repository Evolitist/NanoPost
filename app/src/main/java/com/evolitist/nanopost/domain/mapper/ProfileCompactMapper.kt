package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.data.network.model.ApiProfileCompact
import com.evolitist.nanopost.domain.model.ProfileCompact
import javax.inject.Inject

class ProfileCompactMapper @Inject constructor() {
    fun fromApiToModel(api: ApiProfileCompact) = ProfileCompact(
        id = api.id,
        username = api.username,
        displayName = api.displayName,
        avatarUrl = api.avatarUrl,
        subscribed = api.subscribed,
    )
}
