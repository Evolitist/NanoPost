package com.evolitist.nanopost.presentation.ui.images

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.domain.model.ImageSize
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.ui.view.PullRefreshBox
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

@Serializable
data class ImagesRoute(val id: String? = null)

@Composable
fun ImagesRoute(
    modifier: Modifier = Modifier,
    viewModel: ImagesViewModel = hiltViewModel(),
    onCloseClick: () -> Unit,
    onImageClick: (String) -> Unit,
) {
    val images = viewModel.imagesPagingDataFlow.collectAsLazyPagingItems()

    ImagesScreen(
        images = images,
        onCloseClick = onCloseClick,
        onImageClick = onImageClick,
        modifier = modifier,
    )
}

@Composable
fun ImagesScreen(
    images: LazyPagingItems<Image>,
    onCloseClick: () -> Unit,
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
                title = { Text(stringResource(R.string.images)) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        PullRefreshBox(
            data = images,
            indicatorPadding = padding,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    count = images.itemCount,
                    key = images.itemKey(Image::id),
                ) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    ) {
                        images[index]?.let { image ->
                            AsyncImage(
                                model = image.sizes.last().url,
                                fallback = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                                contentDescription = image.contentDescription,
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

                loadState(images.loadState.append, images::retry)
            }
        }
    }
}

@Preview
@Composable
private fun ImagesScreen_Preview() {
    val mockData = List(15) {
        Image(
            id = it.toString(),
            owner = ProfileCompact("", "username", null, null, null, false),
            dateCreated = Clock.System.now().minus(7.days).epochSeconds,
            contentDescription = null,
            blurHash = null,
            sizes = persistentListOf(
                ImageSize(200, 200, "https://cataas.com/cat?type=square")
            ),
        )
    }
    val pagingData = PagingData.from(mockData)
    val mockDataFlow = MutableStateFlow(pagingData)

    ImagesScreen(
        images = mockDataFlow.collectAsLazyPagingItems(),
        onCloseClick = {},
        onImageClick = {},
    )
}
