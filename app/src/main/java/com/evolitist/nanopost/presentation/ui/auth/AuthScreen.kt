package com.evolitist.nanopost.presentation.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
    val validationState by viewModel.usernameValidation

    var username by rememberSaveable { mutableStateOf("") }
    val usernameFocusRequester = remember { FocusRequester() }
    var password by rememberSaveable { mutableStateOf("") }
    val passwordFocusRequester = remember { FocusRequester() }

    fun tryFocus() {
        /*if (authState == AuthState.CheckUsername) {
            usernameFocusRequester.requestFocus()
        } else {
            passwordFocusRequester.requestFocus()
        }*/
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
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

            val isError = validationState != null
            val errorMessage = when (validationState) {
                UsernameCheckResult.TooShort -> "Username must be longer than 3 characters"
                UsernameCheckResult.TooLong -> "Username must not be longer than 16 characters"
                UsernameCheckResult.InvalidCharacters -> "Username can only contain lowercase letters, dots and underscores"
                else -> null
            }


            HelperTextField(
                value = username,
                onValueChange = { username = it },
                singleLine = true,
                label = { Text("Username") },
                helper = { Text(errorMessage.orEmpty()) },
                showHelper = isError,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    viewModel.authButtonClick(username, password, ::tryFocus)
                },
                enabled = authState is AuthScreenState.CheckUsername,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocusRequester)
                    .autofill(
                        autofillTypes = listOf(AutofillType.Username).immutable(),
                        onFill = { username = it },
                    ),
            )

            AnimatedVisibility(visible = authState.showPasswordField) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions {
                        viewModel.authButtonClick(username, password, ::tryFocus)
                    },
                    visualTransformation = remember { PasswordVisualTransformation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusRequester(passwordFocusRequester)
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
                    viewModel.authButtonClick(username, password, ::tryFocus)
                },
            )
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
        AuthScreenState.CheckUsername -> Text("Continue")
        AuthScreenState.SignIn -> Text("Sign In")
        AuthScreenState.Register -> Text("Register")
        is AuthScreenState.Loading -> Text("Loading...")
        is AuthScreenState.Error<*> -> AuthButtonText(state.previousState)
    }
}

@Composable
fun ColumnScope.HelperText(
    isError: Boolean,
    helperText: String?,
) {
    val helperColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val helperStyle = if (isError) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.bodySmall
    }

    AnimatedVisibility(
        visible = helperText != null,
        modifier = Modifier.padding(start = 16.dp),
    ) {
        Text(
            text = helperText.orEmpty(),
            style = helperStyle,
            color = helperColor,
        )
    }
}
