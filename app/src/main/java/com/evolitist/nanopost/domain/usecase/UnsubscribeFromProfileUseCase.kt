package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.ProfileRepository
import javax.inject.Inject

class UnsubscribeFromProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {

    suspend operator fun invoke(profileId: String) {
        profileRepository.unsubscribe(profileId)
    }
}
