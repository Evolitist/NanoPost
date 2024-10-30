package com.evolitist.nanopost.presentation.ui.create.profile

import androidx.lifecycle.ViewModel
import com.evolitist.nanopost.domain.model.UsernameCheckResult
import com.evolitist.nanopost.domain.usecase.CheckUsernameUseCase
import com.evolitist.nanopost.domain.usecase.ValidateUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val validateUsernameUseCase: ValidateUsernameUseCase,
    private val checkUsernameUseCase: CheckUsernameUseCase,
) : ViewModel() {

    private val usernameStateFlow = MutableStateFlow<String?>(null)

    private val usernameCheckResultFlow = usernameStateFlow.filterNotNull()
        .transform { username ->
            validateUsernameUseCase(username)?.let {
                emit(CreateProfileScreenState.CheckUsername.error(it))
            } ?: run {
                emit(CreateProfileScreenState.CheckUsername.loading())
                when (val checkResult = checkUsernameUseCase(username)) {
                    UsernameCheckResult.Free -> emit(CreateProfileScreenState.FillData)
                    else -> CreateProfileScreenState.CheckUsername.error(checkResult)
                }
            }
        }
}
