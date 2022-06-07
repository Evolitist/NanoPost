package com.evolitist.nanopost.presentation.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import com.evolitist.nanopost.presentation.extensions.immutable
import com.evolitist.nanopost.presentation.extensions.items
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.ui.view.CenterAlignedTopAppBar
import com.evolitist.nanopost.presentation.ui.view.ImagesCard
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.evolitist.nanopost.presentation.ui.view.ProfileCard
import com.evolitist.nanopost.presentation.ui.view.SmallFABLayout
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    profileId: String? = null,
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onImagesClick: (String?) -> Unit,
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
) {
    val data = viewModel.profilePagingItems
    val swipeRefreshState = rememberSwipeRefreshState(
        data.loadState.refresh == LoadState.Loading
    )

    LaunchedEffect(profileId) {
        viewModel.setProfileId(profileId)
    }

    val topAppBarState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(topAppBarState) {
        TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (profileId != null) {
                        IconButton(
                            content = { Icon(Icons.Rounded.ArrowBack, contentDescription = null) },
                            onClick = onCloseClick,
                        )
                    }
                },
                title = { Text("Profile") },
                actions = {
                    IconButton(
                        content = { Icon(Icons.Rounded.Logout, contentDescription = null) },
                        onClick = onLogoutClick,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            SmallFABLayout(
                items = { listOf(CreateActions.Profile, CreateActions.Post) },
                content = { Icon(Icons.Rounded.Add, contentDescription = null) },
                label = {
                    when (it) {
                        CreateActions.Post -> Text("Post")
                        CreateActions.Profile -> Text("Profile")
                    }
                },
                button = {
                    when (it) {
                        CreateActions.Post -> SmallFloatingActionButton(
                            onClick = onCreatePostClick,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            content = { Icon(Icons.Rounded.PostAdd, contentDescription = null) },
                        )
                        CreateActions.Profile -> SmallFloatingActionButton(
                            onClick = onCreateProfileClick,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            content = { Icon(Icons.Rounded.GroupAdd, contentDescription = null) },
                        )
                    }
                },
            )
        },
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = data::refresh,
            modifier = Modifier.padding(padding),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                items(
                    items = data,
                    key = { it.id ?: "${it.type}_${profileId}" },
                    contentType = { it.type },
                ) { element ->
                    when (element) {
                        is ProfileElement.ProfileItem -> ProfileCard(
                            profile = element.profile,
                            buttonText = "Edit",
                            onButtonClick = {},
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        is ProfileElement.ImagesItem -> ImagesCard(
                            images = element.images.immutable(),
                            onCardClick = { onImagesClick(profileId) },
                            onImageClick = onImageClick,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        is ProfileElement.PostItem -> PostCard(
                            post = element.post,
                            onClick = onPostClick,
                            onImageClick = onImageClick,
                            onHeaderClick = {
                                if (profileId != it && profileId != null) {
                                    onProfileClick(it)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        else -> Unit
                    }
                }
            }
        }
    }
}
