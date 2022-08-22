package com.evolitist.nanopost.presentation.ui.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.ui.view.CenterAlignedTopAppBar
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun PostsScreen(
    viewModel: PostsViewModel = hiltViewModel(),
    profileId: String? = null,
    onCloseClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
) {
    val data = viewModel.postsPagingDataFlow.collectAsLazyPagingItems()
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
                title = { Text(stringResource(R.string.posts)) },
                scrollBehavior = scrollBehavior,
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
