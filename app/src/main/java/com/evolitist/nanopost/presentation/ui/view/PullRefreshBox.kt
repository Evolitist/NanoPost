package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun PullRefreshBox(
    data: LazyPagingItems<*>,
    modifier: Modifier = Modifier,
    indicatorPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    if (isRefreshing) {
        LaunchedEffect(true) {
            data.refresh()
        }
    }

    LaunchedEffect(data.loadState.refresh) {
        if (data.loadState.refresh != LoadState.Loading) {
            isRefreshing = false
        }
    }

    Box(
        modifier = modifier.pullToRefresh(
            isRefreshing = isRefreshing,
            state = state,
            onRefresh = { isRefreshing = true },
        )
    ) {
        content()
        Indicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(indicatorPadding),
            isRefreshing = isRefreshing,
            state = state,
        )
    }
}
