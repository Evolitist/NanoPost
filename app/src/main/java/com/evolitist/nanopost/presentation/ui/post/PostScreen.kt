package com.evolitist.nanopost.presentation.ui.post

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.ui.view.DropdownMenuIconButton
import com.evolitist.nanopost.presentation.ui.view.PostContent
import com.evolitist.nanopost.presentation.ui.view.insets.CenterAlignedTopAppBar
import com.evolitist.nanopost.presentation.ui.view.insets.Scaffold

@Composable
fun PostScreen(
    viewModel: PostViewModel = hiltViewModel(),
    postId: String,
    onCloseClick: () -> Unit,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
) {
    val post by viewModel.postStateFlow.collectAsState()

    LaunchedEffect(postId) {
        viewModel.setPostId(postId)
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
                        content = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) },
                        onClick = onCloseClick,
                    )
                },
                title = { Text(stringResource(R.string.post)) },
                actions = {
                    DropdownMenuIconButton(
                        icon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                        content = {
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null) },
                                text = { Text(stringResource(R.string.action_delete)) },
                                onClick = { viewModel.onDeleteClick(postId, onCloseClick) },
                            )
                        },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        post?.let {
            PostContent(
                post = it,
                onImageClick = onImageClick,
                onHeaderClick = onProfileClick,
                modifier = Modifier
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState()),
            )
        }
    }
}
