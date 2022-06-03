package com.evolitist.nanopost.domain.usecase

import com.evolitist.nanopost.data.repository.ProfileRepository
import com.evolitist.nanopost.domain.model.Profile
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getLocalUserIdUseCase: GetLocalUserIdUseCase,
) {

    suspend operator fun invoke(id: String?): Profile {
        val profileId = id ?: getLocalUserIdUseCase()!!
        return profileRepository.getProfile(profileId)
    }
}
