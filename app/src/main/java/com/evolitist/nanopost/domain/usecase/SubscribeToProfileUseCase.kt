package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.ProfileRepository
import javax.inject.Inject

class SubscribeToProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(profileId: String) {
        profileRepository.subscribe(profileId)
    }
}
