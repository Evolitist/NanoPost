package com.evolitist.nanopost.presentation.ui.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.presentation.ui.view.DropdownMenuIconButton
import com.evolitist.nanopost.presentation.ui.view.Header
import com.evolitist.nanopost.presentation.ui.view.HeaderStyle
import com.evolitist.nanopost.presentation.ui.view.insets.CenterAlignedTopAppBar
import java.text.SimpleDateFormat

@Composable
fun ImageScreen(
    viewModel: ImageViewModel = hiltViewModel(),
    imageId: String,
    onCloseClick: () -> Unit,
) {
    var showBars by remember { mutableStateOf(true) }
    val overlay = updateTransition(showBars, label = "Overlay transition")
    val image by viewModel.imageStateFlow.collectAsState()

    LaunchedEffect(imageId) {
        viewModel.setImageId(imageId)
    }

    Surface {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            image?.let {
                TransformableImage(image = it) { showBars = !showBars }
            }

            val scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = .5f)
            overlay.AnimatedVisibility(
                visible = { it },
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            content = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) },
                            onClick = onCloseClick,
                        )
                    },
                    title = {},
                    actions = {
                        DropdownMenuIconButton(
                            icon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                            content = {
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null) },
                                    text = { Text(stringResource(R.string.action_delete)) },
                                    onClick = { viewModel.onDeleteClick(imageId, onCloseClick) },
                                )
                            },
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = scrimColor,
                    ),
                )
            }

            image?.let {
                overlay.AnimatedVisibility(
                    visible = { it },
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(scrimColor)
                            .padding(top = 16.dp)
                            .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Bottom)),
                    ) {
                        val dateCreated = remember(it.dateCreated) {
                            SimpleDateFormat.getDateTimeInstance().format(it.dateCreated)
                        }

                        Header(
                            style = HeaderStyle.Image,
                            profile = it.owner,
                            subtitle = { Text(dateCreated) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransformableImage(
    image: Image,
    onTap: () -> Unit,
) {
    val imageSize = image.sizes.first()
    val zoomableState = rememberZoomableState()

    AsyncImage(
        model = imageSize.url,
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .zoomable(
                state = zoomableState,
                onTap = onTap,
            ),
    )
}
