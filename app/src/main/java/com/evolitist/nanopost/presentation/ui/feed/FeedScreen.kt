package com.evolitist.nanopost.presentation.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.items
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.ui.view.CenterAlignedTopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.evolitist.nanopost.presentation.ui.view.SmallFABLayout

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
) {
    val data = viewModel.feedPagingItems
    val swipeRefreshState = rememberSwipeRefreshState(
        data.loadState.refresh == LoadState.Loading
    )

    val topAppBarState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(topAppBarState) {
        TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.feed)) },
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
                    key = { it.id },
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
            }
        }
    }
}
