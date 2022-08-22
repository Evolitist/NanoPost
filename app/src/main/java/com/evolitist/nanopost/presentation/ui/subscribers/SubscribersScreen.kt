package com.evolitist.nanopost.presentation.ui.subscribers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.evolitist.nanopost.presentation.extensions.items
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.ui.view.Avatar
import com.evolitist.nanopost.presentation.ui.view.CenterAlignedTopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SubscribersScreen(
    viewModel: SubscribersViewModel = hiltViewModel(),
    profileId: String?,
    onCloseClick: () -> Unit,
    onProfileClick: (String) -> Unit,
) {
    val data = viewModel.subscribersPagingDataFlow.collectAsLazyPagingItems()
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
                title = { Text(stringResource(R.string.subscribers)) },
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
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                items(
                    items = data,
                    key = ProfileCompact::id,
                ) { profile ->
                    profile?.let {
                        ListItem(
                            leadingContent = {
                                Avatar(
                                    profile = it,
                                    monogramFontSize = 16.sp,
                                    modifier = Modifier.size(40.dp),
                                )
                            },
                            headlineText = { Text(it.displayName ?: it.username) },
                            modifier = Modifier.clickable { onProfileClick(it.id) },
                        )

                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }

                loadState(data.loadState.append, data::retry)
            }
        }
    }
}
