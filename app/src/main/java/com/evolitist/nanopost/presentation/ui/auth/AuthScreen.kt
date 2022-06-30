package com.evolitist.nanopost.presentation.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.UsernameCheckResult
import com.evolitist.nanopost.presentation.extensions.immutable
import com.evolitist.nanopost.presentation.ui.view.HelperTextField
import com.evolitist.nanopost.presentation.ui.view.autofill

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val authState by viewModel.authScreenState

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        authState.let {
            if (it is AuthScreenState.Error && it.error is Exception) {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = it.error.localizedMessage.orEmpty(),
                    actionLabel = "Retry",
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    viewModel.authButtonClick(username, password)
                } else {
                    viewModel.clearErrors()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            val errorMessage = (authState as? AuthScreenState.Error)?.let {
                when (it.error) {
                    UsernameCheckResult.TooShort -> stringResource(R.string.auth_error_username_too_short)
                    UsernameCheckResult.TooLong -> stringResource(R.string.auth_error_username_too_long)
                    UsernameCheckResult.InvalidCharacters -> stringResource(R.string.auth_error_username_invalid_characters)
                    else -> null
                }
            }

            HelperTextField(
                value = username,
                onValueChange = { username = it; viewModel.clearErrors() },
                singleLine = true,
                label = { Text(stringResource(R.string.auth_hint_username)) },
                helper = { Text(errorMessage.orEmpty()) },
                showHelper = errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    viewModel.authButtonClick(username, password)
                },
                enabled = authState.success() is AuthScreenState.CheckUsername,
                isError = errorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .autofill(
                        autofillTypes = listOf(AutofillType.Username).immutable(),
                        onFill = { username = it },
                    ),
            )

            AnimatedVisibility(visible = authState.showPasswordField) {
                TextField(
                    value = password,
                    onValueChange = { password = it; viewModel.clearErrors() },
                    singleLine = true,
                    label = { Text(stringResource(R.string.auth_hint_password)) },
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions {
                        keyboardController?.hide()
                        viewModel.authButtonClick(username, password)
                    },
                    visualTransformation = remember { PasswordVisualTransformation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .autofill(
                            autofillTypes = listOf(AutofillType.Password).immutable(),
                            onFill = { password = it },
                        ),
                )
            }
            Button(
                content = {
                    Crossfade(
                        targetState = authState,
                        content = { AuthButtonText(it) },
                    )
                },
                enabled = authState !is AuthScreenState.Loading,
                onClick = {
                    if (authState.showPasswordField) {
                        keyboardController?.hide()
                    }
                    viewModel.authButtonClick(username, password)
                },
                modifier = Modifier.padding(top = 16.dp),
            )
            // TODO implement "non-authorized" mode
            /*OutlinedButton(
                content = { Text("Skip") },
                onClick = viewModel::skipButtonClick,
                enabled = !isLoading,
            )*/
        }
    }
}

@Composable
private fun AuthButtonText(state: AuthScreenState) {
    when (state) {
        AuthScreenState.CheckUsername -> Text(stringResource(R.string.auth_action_continue))
        AuthScreenState.SignIn -> Text(stringResource(R.string.auth_action_sign_in))
        AuthScreenState.Register -> Text(stringResource(R.string.auth_action_register))
        is AuthScreenState.Loading -> Text(stringResource(R.string.auth_action_loading))
        is AuthScreenState.Error -> AuthButtonText(state.previousState)
    }
}
