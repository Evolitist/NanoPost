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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.navigation.sharedBounds
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.utils.ParcelableListSaver
import kotlinx.serialization.Serializable

@Serializable
data object CreatePostRoute

@Composable
fun CreatePostScreen(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = hiltViewModel(),
) {
    val (text, setText) = rememberSaveable { mutableStateOf("") }
    val images = rememberSaveable(saver = ParcelableListSaver()) { mutableStateListOf<Uri>() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(images::add)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .sharedBounds(CreateActions.Post)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.action_close),
                            )
                        },
                        onClick = onCloseClick,
                    )
                },
                title = { Text(stringResource(R.string.create_post)) },
                actions = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = stringResource(R.string.action_done),
                            )
                        },
                        onClick = {
                            keyboardController?.hide()
                            viewModel.launchCreatePost(text, images)
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
                    .verticalScroll(rememberScrollState()),
            ) {
                TextField(
                    value = text,
                    onValueChange = setText,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    shape = RectangleShape,
                    placeholder = { Text(stringResource(R.string.create_post_placeholder)) },
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
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = stringResource(R.string.create_post_add_image),
                        )
                    },
                    label = { Text(stringResource(R.string.create_post_add_image)) },
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
private fun <T : Any> RemovableImage(
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
                    onClick = { onCancelClick(image) },
                ),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.background),
            )
            Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
