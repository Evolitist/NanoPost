package com.evolitist.nanopost.presentation.ui.subscribers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.ui.view.Avatar
import com.evolitist.nanopost.presentation.ui.view.PullRefreshBox
import kotlinx.serialization.Serializable

@Serializable
data class SubscribersRoute(val id: String? = null)

@Composable
fun SubscribersRoute(
    onCloseClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubscribersViewModel = hiltViewModel(),
) {
    val subscribers = viewModel.subscribersPagingDataFlow.collectAsLazyPagingItems()

    SubscribersScreen(
        subscribers = subscribers,
        onCloseClick = onCloseClick,
        onProfileClick = onProfileClick,
        modifier = modifier,
    )
}

@Composable
fun SubscribersScreen(
    subscribers: LazyPagingItems<ProfileCompact>,
    onCloseClick: () -> Unit,
    onProfileClick: (String) -> Unit,
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
                title = { Text(stringResource(R.string.subscribers)) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        PullRefreshBox(
            data = subscribers,
            indicatorPadding = padding,
        ) {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    count = subscribers.itemCount,
                    key = subscribers.itemKey(ProfileCompact::id),
                ) { index ->
                    subscribers[index]?.let { profile ->
                        ListItem(
                            leadingContent = {
                                Avatar(
                                    profile = profile,
                                    monogramFontSize = 16.sp,
                                    modifier = Modifier.size(40.dp),
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = profile.displayName ?: profile.username,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            modifier = Modifier.clickable { onProfileClick(profile.id) },
                        )

                        if (index <= subscribers.itemCount) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }

                loadState(subscribers.loadState.append, subscribers::retry)
            }
        }
    }
}
