package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessStarted
import coil3.compose.AsyncImage
import com.evolitist.nanopost.domain.model.Post
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Suppress("UnusedReceiverParameter")
@Composable
fun ColumnScope.PostContent(
    post: Post,
    onImageClick: (String) -> Unit,
    onHeaderClick: (String) -> Unit,
    isStandalone: Boolean = true,
) {
    val dateCreated = remember(post.dateCreated) {
        Instant.fromEpochSeconds(post.dateCreated)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString()
    }

    val hasText by remember {
        derivedStateOf { !post.text.isNullOrBlank() }
    }

    val onHeaderClickState by rememberUpdatedState { onHeaderClick(post.owner.id) }
    Header(
        style = HeaderStyle.Post,
        profile = post.owner,
        subtitle = dateCreated,
        onClick = dropUnlessStarted { onHeaderClickState() },
    )

    if (!post.text.isNullOrBlank()) {
        val text = @Composable {
            Text(
                text = post.text,
                maxLines = if (isStandalone) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
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
            Box(
                modifier = if (hasText) {
                    Modifier.padding(top = 16.dp)
                } else {
                    Modifier
                },
            ) {
                val pageCount by remember {
                    derivedStateOf { post.images.size }
                }
                val pagerState = rememberPagerState { pageCount }
                HorizontalPager(
                    state = pagerState,
                    key = { post.images[it].id },
                    modifier = Modifier.aspectRatio(1f),
                ) {
                    val image = post.images[it]
                    val onImageClickState by rememberUpdatedState { onImageClick(image.id) }
                    AsyncImage(
                        model = image.sizes.first().url,
                        placeholder = placeholderPainter,
                        error = placeholderPainter,
                        contentDescription = image.contentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = dropUnlessStarted { onImageClickState() }),
                    )
                }

                val counterText by remember {
                    derivedStateOf { "${pagerState.currentPage.inc()}/$pageCount" }
                }
                Text(
                    text = counterText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(50),
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        } else {
            post.images.forEach { image ->
                val onImageClickState by rememberUpdatedState { onImageClick(image.id) }
                AsyncImage(
                    model = image.sizes.first().url,
                    error = placeholderPainter,
                    contentDescription = image.contentDescription,
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
                        .clickable(onClick = dropUnlessStarted { onImageClickState() }),
                )
            }
        }
    }

    LikeButton(
        postId = post.id,
        initialLikes = post.likes,
        modifier = Modifier.padding(16.dp),
    )
}
