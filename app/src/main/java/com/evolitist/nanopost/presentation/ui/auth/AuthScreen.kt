package com.evolitist.nanopost.presentation.ui.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.PasswordCheckResult
import com.evolitist.nanopost.domain.model.UsernameCheckResult
import com.evolitist.nanopost.presentation.ui.LocalSnackbarHost
import com.evolitist.nanopost.presentation.ui.view.autofill
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoute

@Composable
fun AuthRoute(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    AuthScreen(
        authState = viewModel.authScreenState.value,
        onAuthClick = viewModel::authButtonClick,
        clearErrors = viewModel::clearErrors,
        modifier = modifier,
    )
}

@Composable
fun AuthScreen(
    authState: AuthScreenState,
    onAuthClick: (String, String, String) -> Unit,
    clearErrors: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = LocalSnackbarHost.current

    val retryLabel = stringResource(R.string.action_retry)
    LaunchedEffect(authState) {
        authState.let { state ->
            if (state is AuthScreenState.Error && state.error is Exception) {
                Log.e("Auth", null, state.error)
                val snackbarResult = snackbarHostState?.showSnackbar(
                    message = state.error.localizedMessage.orEmpty(),
                    actionLabel = retryLabel,
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    onAuthClick(username, password, confirmPassword)
                } else {
                    clearErrors()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            val usernameErrorMessage = (authState as? AuthScreenState.Error)?.let {
                when (it.error) {
                    UsernameCheckResult.TooShort -> stringResource(R.string.username_validation_too_short)
                    UsernameCheckResult.TooLong -> stringResource(R.string.username_validation_too_long)
                    UsernameCheckResult.InvalidCharacters -> stringResource(R.string.username_validation_malformed)
                    else -> null
                }
            }
            TextField(
                value = username,
                onValueChange = { username = it; clearErrors() },
                singleLine = true,
                label = { Text(stringResource(R.string.auth_hint_username)) },
                supportingText = usernameErrorMessage?.let {
                    { Text(it) }
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    onAuthClick(username, password, confirmPassword)
                },
                enabled = authState.success() is AuthScreenState.CheckUsername,
                isError = usernameErrorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .autofill(
                        autofillTypes = persistentListOf(AutofillType.Username),
                        onFill = { username = it },
                    ),
            )

            AnimatedVisibility(visible = authState.showPasswordField) {
                val passwordErrorMessage = (authState as? AuthScreenState.Error)?.let {
                    when (it.error) {
                        PasswordCheckResult.TooShort -> stringResource(R.string.password_validation_too_short)
                        else -> null
                    }
                }
                TextField(
                    value = password,
                    onValueChange = { password = it; clearErrors() },
                    singleLine = true,
                    label = { Text(stringResource(R.string.auth_hint_password)) },
                    supportingText = passwordErrorMessage?.let {
                        { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = if (authState.showConfirmPasswordField) ImeAction.Next else ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions {
                        keyboardController?.hide()
                        onAuthClick(username, password, confirmPassword)
                    },
                    isError = passwordErrorMessage != null,
                    visualTransformation = remember { PasswordVisualTransformation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .autofill(
                            autofillTypes = persistentListOf(AutofillType.Password),
                            onFill = { password = it },
                        ),
                )
            }
            AnimatedVisibility(visible = authState.showConfirmPasswordField) {
                val confirmPasswordErrorMessage = (authState as? AuthScreenState.Error)?.let {
                    when (it.error) {
                        PasswordCheckResult.NotEqual -> stringResource(R.string.password_validation_not_equal)
                        else -> null
                    }
                }
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; clearErrors() },
                    singleLine = true,
                    label = { Text(stringResource(R.string.auth_hint_confirm_password)) },
                    supportingText = confirmPasswordErrorMessage?.let {
                        { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions {
                        keyboardController?.hide()
                        onAuthClick(username, password, confirmPassword)
                    },
                    isError = confirmPasswordErrorMessage != null,
                    visualTransformation = remember { PasswordVisualTransformation() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Button(
                content = {
                    Crossfade(
                        label = "Auth button text crossfade",
                        targetState = authState,
                        content = { AuthButtonText(it) },
                    )
                },
                enabled = authState !is AuthScreenState.Loading,
                onClick = {
                    if (authState.showPasswordField) {
                        keyboardController?.hide()
                    }
                    onAuthClick(username, password, confirmPassword)
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
        is AuthScreenState.CheckUsername -> Text(stringResource(R.string.auth_action_continue))
        is AuthScreenState.SignIn -> Text(stringResource(R.string.auth_action_sign_in))
        is AuthScreenState.Register -> Text(stringResource(R.string.auth_action_register))
        is AuthScreenState.Loading -> Text(stringResource(R.string.auth_action_loading))
        is AuthScreenState.Error -> AuthButtonText(state.previousState)
    }
}

@Preview
@Composable
private fun AuthScreen_Preview() {
    AuthScreen(
        authState = AuthScreenState.Register(null, null),
        onAuthClick = { _, _, _ -> },
        clearErrors = {},
    )
}
