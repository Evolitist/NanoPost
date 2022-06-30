package com.evolitist.nanopost.domain.usecase

import androidx.paging.PagingData
import com.evolitist.nanopost.data.repository.ProfileRepository
import com.evolitist.nanopost.domain.model.ProfileCompact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscribersUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getLocalUserIdUseCase: GetLocalUserIdUseCase,
) {

    suspend operator fun invoke(profileId: String?): Flow<PagingData<ProfileCompact>> {
        return profileRepository.getSubscribers(
            profileId ?: getLocalUserIdUseCase()!!,
        )
    }
}
