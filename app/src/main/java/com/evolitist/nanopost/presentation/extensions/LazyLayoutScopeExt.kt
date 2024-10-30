package com.evolitist.nanopost.presentation.extensions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.evolitist.nanopost.R

fun LazyListScope.loadState(
    state: LoadState,
    onRetryClick: () -> Unit,
) {
    when (state) {
        is LoadState.NotLoading -> Unit
        LoadState.Loading -> item(contentType = "LazyListLoading") {
            LoadingState(
                modifier = Modifier.fillParentMaxWidth(),
            )
        }
        is LoadState.Error -> item(contentType = "LazyListError") {
            ErrorState(
                state = state,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillParentMaxWidth(),
            )
        }
    }
}

fun LazyGridScope.loadState(
    state: LoadState,
    onRetryClick: () -> Unit,
) {
    when (state) {
        is LoadState.NotLoading -> Unit
        LoadState.Loading -> item(
            contentType = "LazyGridLoading",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            LoadingState(
                modifier = Modifier.fillMaxSize(),
            )
        }
        is LoadState.Error -> item(
            contentType = "LazyGridError",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            ErrorState(
                state = state,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
fun ErrorState(
    state: LoadState.Error,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            text = state.error.localizedMessage ?: state.error.message.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )

        TextButton(
            content = { Text(stringResource(R.string.action_retry)) },
            onClick = onRetryClick,
        )
    }
}
