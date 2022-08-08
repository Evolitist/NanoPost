package com.evolitist.nanopost.presentation.ui.create.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.ui.view.Avatar
import com.evolitist.nanopost.presentation.ui.view.CenterAlignedTopAppBar
import com.evolitist.nanopost.presentation.ui.view.HelperTextField
import com.evolitist.nanopost.presentation.worker.CreateProfileWorker
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPagerApi::class)
@Composable
fun CreateProfileScreen(
    onCloseClick: () -> Unit,
) {
    val (username, setUsername) = rememberSaveable { mutableStateOf("") }
    val (displayName, setDisplayName) = rememberSaveable { mutableStateOf("") }
    val (bio, setBio) = rememberSaveable { mutableStateOf("") }
    var avatarUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { avatarUri = it }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = remember(topAppBarState) {
        TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    }
    Scaffold(
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
                        onClick = {
                            keyboardController?.hide()
                            WorkManager.getInstance(context)
                                .enqueue(CreateProfileWorker.newRequest(username, displayName, bio, avatarUri))
                            onCloseClick()
                        },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        val scope = rememberCoroutineScope()
        val state = rememberPagerState()

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            HorizontalPager(
                count = 2,
                state = state,
                userScrollEnabled = false,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    0 -> UsernamePage(
                        username = username,
                        onUsernameChange = setUsername,
                        onNextClick = {
                            scope.launch { state.animateScrollToPage(1) }
                        },
                    )
                    1 -> ProfileDataPage(
                        avatarUri = avatarUri,
                        displayName = displayName,
                        onDisplayNameChange = setDisplayName,
                        bio = bio,
                        onBioChange = setBio,
                        onNextClick = {

                        },
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            ) {
                TextButton(
                    onClick = { /*TODO*/ },
                    content = { Text("Cancel") },
                )

                Button(
                    onClick = {
                        scope.launch { state.animateScrollToPage(1) }
                    },
                    content = { Text("Next") },
                )
            }
        }
    }
}

@Composable
fun UsernamePage(
    username: String,
    onUsernameChange: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        HelperTextField(
            value = username,
            onValueChange = onUsernameChange,
            singleLine = true,
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions { onNextClick() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun ProfileDataPage(
    avatarUri: Uri?,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Avatar(
                model = avatarUri,
                monogram = { Icon(Icons.Rounded.AddPhotoAlternate, null) },
            )

            TextField(
                value = displayName,
                onValueChange = onDisplayNameChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {  },
                modifier = Modifier.weight(1f),
            )
        }

        TextField(
            value = bio,
            onValueChange = onBioChange,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions { onNextClick() },
        )
    }
}
