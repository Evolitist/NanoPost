package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.ProfileRepository
import com.evolitist.nanopost.domain.model.ImageInfo
import com.evolitist.nanopost.domain.model.Profile
import javax.inject.Inject

class UploadProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(
        username: String,
        displayName: String?,
        bio: String?,
        avatar: ImageInfo?,
        progressCallback: (Float) -> Unit = {},
    ): Profile {
        return profileRepository.putProfile(
            username = username,
            displayName = displayName,
            bio = bio,
            avatar = avatar,
            progressCallback = progressCallback,
        )
    }
}
