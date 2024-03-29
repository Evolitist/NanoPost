package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.evolitist.nanopost.domain.model.Post
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import java.text.SimpleDateFormat

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostContent(
    post: Post,
    onImageClick: (String) -> Unit,
    onHeaderClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isStandalone: Boolean = true,
) {
    Column(
        modifier = modifier.padding(bottom = if (post.images.isEmpty()) 16.dp else 0.dp),
    ) {
        val dateCreated = remember(post.dateCreated) {
            SimpleDateFormat.getDateTimeInstance().format(post.dateCreated)
        }

        val hasText = remember(post.text) { !post.text.isNullOrBlank() }

        Header(
            style = HeaderStyle.Post,
            profile = post.owner,
            subtitle = { Text(dateCreated) },
            onClick = { onHeaderClick(post.owner.id) },
        )

        if (!post.text.isNullOrBlank()) {
            val text = @Composable {
                Text(
                    text = post.text,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            if (isStandalone) {
                SelectionContainer {
                    text()
                }
            } else {
                text()
            }
        }
        if (post.images.isNotEmpty()) {
            val placeholderColor = MaterialTheme.colorScheme.surfaceVariant
            val placeholderPainter = remember { ColorPainter(placeholderColor) }
            if (!isStandalone && post.images.size > 1) {
                HorizontalPager(
                    count = post.images.size,
                    key = { post.images[it].id },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .then(
                            if (hasText) {
                                Modifier.padding(top = 16.dp)
                            } else {
                                Modifier
                            }
                        ),
                ) {
                    val image = post.images[it]
                    AsyncImage(
                        model = image.sizes.first().url,
                        error = placeholderPainter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onImageClick(image.id)
                            },
                    )
                }
            } else {
                post.images.forEach { image ->
                    AsyncImage(
                        model = image.sizes.first().url,
                        error = placeholderPainter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .then(
                                if (hasText) {
                                    Modifier.padding(top = 16.dp)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable {
                                onImageClick(image.id)
                            },
                    )
                }
            }
        }
    }
}
