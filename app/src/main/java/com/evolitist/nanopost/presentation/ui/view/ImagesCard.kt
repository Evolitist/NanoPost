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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.presentation.extensions.ImmutableWrapper

@Composable
fun ImagesCard(
    images: ImmutableWrapper<List<Image>>,
    onCardClick: () -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onCardClick,
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
                    text = "Images",
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
                        images.data.getOrNull(it)?.let { image ->
                            AsyncImage(
                                image.sizes.last().url,
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
            }
        }
    }
}
