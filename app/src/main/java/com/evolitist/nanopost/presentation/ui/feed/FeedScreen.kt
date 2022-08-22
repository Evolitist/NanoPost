package com.evolitist.nanopost.presentation.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.presentation.extensions.copy
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.ui.MainViewModel
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.ui.view.Avatar
import com.evolitist.nanopost.presentation.ui.view.FloatingAppBar
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.evolitist.nanopost.presentation.ui.view.SmallFABLayout
import com.evolitist.nanopost.presentation.ui.view.floatingScrollBehavior
import com.evolitist.nanopost.presentation.ui.view.insets.Scaffold
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String?) -> Unit,
    onImageClick: (String) -> Unit,
) {
    val data = viewModel.feedPagingDataFlow.collectAsLazyPagingItems()
    val swipeRefreshState = rememberSwipeRefreshState(
        data.loadState.refresh == LoadState.Loading
    )

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = remember(topAppBarState) {
        TopAppBarDefaults.floatingScrollBehavior(topAppBarState)
    }
    Scaffold(
        topBar = {
            val activityViewModel = hiltViewModel<MainViewModel>(LocalContext.current as ViewModelStoreOwner)
            val profile by activityViewModel.profileFlow.collectAsState()

            FloatingAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    profile?.let { profile ->
                        IconButton(
                            content = {
                                Avatar(
                                    profile = profile,
                                    monogramFontSize = 12.sp,
                                    modifier = Modifier.size(32.dp),
                                )
                            },
                            onClick = {
                                onProfileClick(null)
                            },
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
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
        },
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = data::refresh,
            indicatorPadding = padding,
        ) {
            val layoutDirection = LocalLayoutDirection.current
            val contentPadding = remember(padding) {
                padding.copy(layoutDirection, top = padding.calculateTopPadding() + 16.dp)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                contentPadding = contentPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                items(
                    items = data,
                    key = Post::id,
                ) { post ->
                    post?.let {
                        PostCard(
                            post = it,
                            onClick = onPostClick,
                            onImageClick = onImageClick,
                            onHeaderClick = onProfileClick,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }

                loadState(data.loadState.append, data::retry)
            }
        }
    }
}
