package com.evolitist.nanopost.presentation.ui.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Likes
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.evolitist.nanopost.presentation.ui.view.DropdownMenuIconButton
import com.evolitist.nanopost.presentation.ui.view.PostContent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
class PostRoute(val id: String)

@Composable
fun PostRoute(
    onCloseClick: () -> Unit,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = hiltViewModel(),
) {
    val post by viewModel.postStateFlow.collectAsStateWithLifecycle()

    PostScreen(
        post = post,
        modifier = modifier,
        onCloseClick = onCloseClick,
        onImageClick = onImageClick,
        onProfileClick = onProfileClick,
    )
}

@Composable
fun PostScreen(
    post: Post?,
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = hiltViewModel(),
    onCloseClick: () -> Unit,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
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
                title = { Text(stringResource(R.string.post)) },
                actions = {
                    DropdownMenuIconButton(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.action_menu),
                            )
                        },
                        content = {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = stringResource(R.string.action_delete),
                                    )
                                },
                                text = { Text(stringResource(R.string.action_delete)) },
                                onClick = { viewModel.onDeleteClick(onCloseClick) },
                            )
                        },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            post?.let {
                PostContent(
                    post = it,
                    onImageClick = onImageClick,
                    onHeaderClick = onProfileClick,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PostScreen_Preview() {
    PostScreen(
        post = Post(
            id = "",
            owner = ProfileCompact("", "username", null, null, null, false),
            dateCreated = 0L,
            text = "",
            images = persistentListOf(),
            likes = Likes(false, 9),
        ),
        onCloseClick = {},
        onImageClick = {},
        onProfileClick = {},
    )
}
