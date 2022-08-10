package com.evolitist.nanopost.presentation.ui.images

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.evolitist.nanopost.R
import com.evolitist.nanopost.presentation.extensions.items
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.ui.view.CenterAlignedTopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun ImagesScreen(
    viewModel: ImagesViewModel = hiltViewModel(),
    profileId: String? = null,
    onCloseClick: () -> Unit,
    onImageClick: (String) -> Unit,
) {
    val data = viewModel.imagesPagingDataFlow.collectAsLazyPagingItems()
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
                title = { Text(stringResource(R.string.images)) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = data::refresh,
            modifier = Modifier.padding(padding),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                items(
                    items = data,
                    key = { it.id },
                ) { image ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .aspectRatio(1f),
                    ) {
                        if (image != null) {
                            AsyncImage(
                                image.sizes.last().url,
                                fallback = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        onImageClick(image.id)
                                    },
                            )
                        }
                    }
                }

                loadState(data.loadState.append, data::retry)
            }
        }
    }
}
