package com.evolitist.nanopost.presentation.ui.create.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.WorkManager
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.navigation.sharedBounds
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.ui.view.Avatar
import com.evolitist.nanopost.presentation.worker.CreateProfileWorker
import kotlinx.serialization.Serializable

private const val BioMaxLength = 140

@Serializable
data object CreateProfileRoute

@Composable
fun CreateProfileScreen(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProfileViewModel = hiltViewModel(),
) {
    val (username, setUsername) = rememberSaveable { mutableStateOf("") }
    val (displayName, setDisplayName) = rememberSaveable { mutableStateOf("") }
    val (bio, setBio) = rememberSaveable { mutableStateOf("") }
    val (avatarUri, setAvatarUri) = rememberSaveable { mutableStateOf<Uri?>(null) }

    val focusManager = LocalFocusManager.current
    val (usernameFocus, displayNameFocus, bioFocus) = remember { FocusRequester.createRefs() }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(setAvatarUri)
    }

    fun launchCreateProfile() {
        keyboardController?.hide()
        WorkManager.getInstance(context)
            .enqueue(CreateProfileWorker.newRequest(username, displayName, bio, avatarUri))
        onCloseClick()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .sharedBounds(CreateActions.Profile),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        content = { Icon(Icons.Rounded.Close, contentDescription = null) },
                        onClick = onCloseClick,
                    )
                },
                title = { Text(stringResource(R.string.create_profile)) },
                actions = {
                    IconButton(
                        content = { Icon(Icons.Rounded.Check, contentDescription = null) },
                        onClick = ::launchCreateProfile,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = setUsername,
                singleLine = true,
                label = { Text("Username") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocus)
                    .focusProperties {
                        next = displayNameFocus
                    },
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Avatar(
                    model = avatarUri?.toString(),
                    blurHash = null,
                    content = { Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null) },
                    modifier = Modifier
                        .size(96.dp)
                        .clickable {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = setDisplayName,
                    singleLine = true,
                    label = { Text("Display name") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(displayNameFocus)
                        .focusProperties {
                            next = bioFocus
                            previous = usernameFocus
                        },
                )
            }
            OutlinedTextField(
                value = bio,
                onValueChange = setBio,
                label = { Text("Bio") },
                supportingText = { Text("${bio.length}/$BioMaxLength") },
                isError = bio.length >= BioMaxLength,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus(force = true)
                    launchCreateProfile()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(bioFocus)
                    .focusProperties {
                        previous = displayNameFocus
                    },
            )
        }
    }
}
