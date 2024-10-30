package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.domain.model.ImageSize
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.wajahatiqbal.blurhash.BlurHashPainter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ImagesCard(
    images: ImmutableList<Image>,
    onCardClick: () -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onCardClickState by rememberUpdatedState(onCardClick)
    val onImageClickState by rememberUpdatedState(onImageClick)

    ElevatedCard(
        onClick = onCardClickState,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.images),
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .weight(1f)
                            .aspectRatio(1f),
                    ) {
                        images.getOrNull(it)?.let { image ->
                            val smallestSize = image.sizes.minBy { it.width }
                            AsyncImage(
                                model = image.sizes.last().url,
                                contentDescription = image.contentDescription,
                                placeholder = BlurHashPainter(
                                    blurHash = image.blurHash,
                                    width = smallestSize.width,
                                    height = smallestSize.height,
                                ),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        onImageClickState(image.id)
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ImagesCard_Preview() {
    val mockImages = List(4) {
        Image(
            id = it.toString(),
            owner = ProfileCompact("", "username", null, null, null, false),
            dateCreated = 0L,
            contentDescription = null,
            blurHash = null,
            sizes = persistentListOf(
                ImageSize(200, 200, "https://cataas.com/cat?type=square"),
            ),
        )
    }.toImmutableList()
    
    ImagesCard(
        images = mockImages,
        onCardClick = {},
        onImageClick = {},
    )
}
