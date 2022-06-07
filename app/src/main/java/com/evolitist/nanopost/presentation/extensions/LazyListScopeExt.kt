package com.evolitist.nanopost.presentation.extensions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.paging.compose.LazyPagingItems
import com.evolitist.nanopost.R

fun <T : Any> LazyListScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: (item: T) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            items.peek(index)?.let(key) ?: index
        },
        contentType = { items.peek(it)?.let(contentType) },
    ) { index ->
        itemContent(items[index])
    }
}

fun LazyListScope.loadState(
    state: LoadState,
    onRetryClick: () -> Unit,
) {
    when (state) {
        is LoadState.NotLoading -> Unit
        LoadState.Loading -> item(contentType = "LazyListLoading") {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillParentMaxWidth(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
        is LoadState.Error -> item(contentType = "LazyListError") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp),
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
    }
}
