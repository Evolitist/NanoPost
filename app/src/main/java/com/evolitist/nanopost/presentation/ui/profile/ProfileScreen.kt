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
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.evolitist.nanopost.presentation.extensions.items
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.ui.view.ImagesCard
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.evolitist.nanopost.presentation.ui.view.ProfileButtonState
import com.evolitist.nanopost.presentation.ui.view.ProfileCard
import com.evolitist.nanopost.presentation.ui.view.SmallFABLayout
import com.evolitist.nanopost.presentation.ui.view.insets.CenterAlignedTopAppBar
import com.evolitist.nanopost.presentation.ui.view.insets.Scaffold
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    profileId: String? = null,
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSubscribersClick: (String?) -> Unit,
    onImagesClick: (String?) -> Unit,
    onPostsClick: (String?) -> Unit,
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
) {
    val data = viewModel.profilePagingDataFlow.collectAsLazyPagingItems()
    val swipeRefreshState = rememberSwipeRefreshState(
        data.loadState.refresh == LoadState.Loading
    )

    LaunchedEffect(profileId) {
        viewModel.setProfileId(profileId)
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
                title = { Text("Profile") },
                actions = {
                    if (profileId == null) {
                        IconButton(
                            content = { Icon(Icons.Rounded.Logout, contentDescription = null) },
                            onClick = onLogoutClick,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (profileId == null) {
                SmallFABLayout(
                    items = persistentListOf(CreateActions.Profile, CreateActions.Post),
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
            }
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
                            buttonState = if (profileId == null) {
                                ProfileButtonState.Edit
                            } else if (element.profile.subscribed) {
                                ProfileButtonState.Unsubscribe
                            } else {
                                ProfileButtonState.Subscribe
                            },
                            onSubscribersClick = { onSubscribersClick(profileId) },
                            onImagesClick = { onImagesClick(profileId) },
                            onPostsClick = { onPostsClick(profileId) },
                            onButtonClick = {
                                if (profileId == null) {
                                    // TODO edit profile
                                } else if (element.profile.subscribed) {
                                    viewModel.unsubscribe(data::refresh)
                                } else {
                                    viewModel.subscribe(data::refresh)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        is ProfileElement.ImagesItem -> ImagesCard(
                            images = element.images,
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

                loadState(data.loadState.append, data::retry)
            }
        }
    }
}
