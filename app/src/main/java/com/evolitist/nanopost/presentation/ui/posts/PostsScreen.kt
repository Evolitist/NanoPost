package com.evolitist.nanopost.presentation.ui.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.extensions.plus
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.evolitist.nanopost.presentation.ui.view.PullRefreshBox
import kotlinx.serialization.Serializable

@Serializable
data class PostsRoute(val id: String? = null)

@Composable
fun PostsRoute(
    onCloseClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostsViewModel = hiltViewModel(),
) {
    val posts = viewModel.postsPagingDataFlow.collectAsLazyPagingItems()

    PostsScreen(
        posts = posts,
        onCloseClick = onCloseClick,
        onPostClick = onPostClick,
        onProfileClick = onProfileClick,
        onImageClick = onImageClick,
        modifier = modifier,
    )
}

@Composable
fun PostsScreen(
    posts: LazyPagingItems<Post>,
    onCloseClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                            )
                        },
                        onClick = onCloseClick,
                    )
                },
                title = { Text(stringResource(R.string.posts)) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        PullRefreshBox(
            data = posts,
            indicatorPadding = padding,
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                contentPadding = padding + PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    count = posts.itemCount,
                    key = posts.itemKey(Post::id),
                    contentType = posts.itemContentType(),
                ) { index ->
                    posts[index]?.let { post ->
                        PostCard(
                            post = post,
                            onClick = onPostClick,
                            onImageClick = onImageClick,
                            onHeaderClick = onProfileClick,
                        )
                    }
                }

                loadState(posts.loadState.append, posts::retry)
            }
        }
    }
}
