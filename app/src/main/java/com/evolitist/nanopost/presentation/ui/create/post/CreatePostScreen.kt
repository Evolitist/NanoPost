package com.evolitist.nanopost.presentation.ui.create.post

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.ui.view.insets.CenterAlignedTopAppBar
import com.evolitist.nanopost.presentation.ui.view.insets.Scaffold
import com.evolitist.nanopost.presentation.utils.ParcelableListSaver
import com.evolitist.nanopost.presentation.worker.CreatePostWorker

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreatePostScreen(
    onCloseClick: () -> Unit,
) {
    val (text, setText) = rememberSaveable { mutableStateOf("") }
    val images = rememberSaveable(saver = ParcelableListSaver()) { mutableStateListOf<Uri>() }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(images::add)
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
                title = { Text(stringResource(R.string.create_post)) },
                actions = {
                    IconButton(
                        content = { Icon(Icons.Rounded.Check, contentDescription = null) },
                        onClick = {
                            keyboardController?.hide()
                            WorkManager.getInstance(context)
                                .enqueue(CreatePostWorker.newRequest(text, images))
                            onCloseClick()
                        },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState()),
            ) {
                TextField(
                    value = text,
                    onValueChange = setText,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    shape = RectangleShape,
                    placeholder = { Text("What's on your mind?") },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                AssistChip(
                    onClick = {
                        imagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    leadingIcon = { Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null) },
                    label = { Text("Add image...") },
                )
            }

            ImagesRow(
                images = images,
                onCancelClick = images::remove,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp),
            )
        }
    }
}

@Composable
private fun <T : Any> ImagesRow(
    images: SnapshotStateList<T>,
    onCancelClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(start = 4.dp, end = 10.dp),
        modifier = modifier,
    ) {
        items(images, key = { it }) { model ->
            RemovableImage(
                image = model,
                onCancelClick = onCancelClick,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
fun <T> RemovableImage(
    image: T,
    onCancelClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier,
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.Center),
        ) {
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(6.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                ) { onCancelClick(image) },
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.background),
            )
            Icon(
                Icons.Rounded.Cancel,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
