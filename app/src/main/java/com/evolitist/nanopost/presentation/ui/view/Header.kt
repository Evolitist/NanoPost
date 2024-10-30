package com.evolitist.nanopost.presentation.ui.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.wajahatiqbal.blurhash.BlurHashPainter

private const val AvatarDefaultSize = 160

@Composable
fun Header(
    style: HeaderStyle,
    profile: ProfileCompact,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val onClickState by rememberUpdatedState(onClick)
    val profileName = remember(profile) {
       profile.displayName ?: profile.username
    }

    ListItem(
        leadingContent = {
            Avatar(
                model = profile.avatarUrl,
                blurHash = profile.avatarBlurHash,
                content = {
                    Text(
                        text = profileName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = style.monogramFontSize(),
                    )
                },
                modifier = Modifier.size(style.avatarSize()),
            )
        },
        headlineContent = {
            Text(
                text = profileName,
                style = style.titleTextStyle(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        colors = ListItemDefaults.colors(containerColor = style.containerColor()),
        modifier = modifier
            .clickable(
                enabled = onClick != null,
                onClick = { onClickState?.invoke() },
            ),
    )
}

@Composable
fun Avatar(
    profile: ProfileCompact,
    monogramFontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    val profileName = remember(profile) {
        profile.displayName ?: profile.username
    }

    Avatar(
        model = profile.avatarUrl,
        blurHash = profile.avatarBlurHash,
        content = {
            Text(
                text = profileName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontSize = monogramFontSize,
            )
        },
        modifier = modifier,
    )
}

@Composable
fun Avatar(
    model: String?,
    blurHash: String?,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
    ) {
        if (model != null) {
            val uri = remember(model) {
                Uri.parse(model) ?: model
            }
            AsyncImage(
                model = uri,
                contentDescription = null,
                placeholder = BlurHashPainter(
                    blurHash = blurHash,
                    width = AvatarDefaultSize,
                    height = AvatarDefaultSize,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.inverseOnSurface,
            ) {
                content()
            }
        }
    }
}

private fun HeaderStyle.avatarSize() = when (this) {
    HeaderStyle.Profile -> 64.dp
    HeaderStyle.Post -> 40.dp
    HeaderStyle.Image -> 40.dp
}

private fun HeaderStyle.monogramFontSize() = when (this) {
    HeaderStyle.Profile -> 32.sp
    HeaderStyle.Post -> 16.sp
    HeaderStyle.Image -> 16.sp
}

@Composable
@ReadOnlyComposable
private fun HeaderStyle.titleTextStyle() = when (this) {
    HeaderStyle.Profile -> MaterialTheme.typography.titleLarge
    HeaderStyle.Post -> MaterialTheme.typography.labelLarge
    HeaderStyle.Image -> MaterialTheme.typography.labelLarge
}

@Composable
@ReadOnlyComposable
private fun HeaderStyle.containerColor() = when (this) {
    HeaderStyle.Profile -> MaterialTheme.colorScheme.surfaceContainerLow
    HeaderStyle.Post -> MaterialTheme.colorScheme.surfaceContainerLow
    HeaderStyle.Image -> Color.Transparent
}

enum class HeaderStyle {
    Profile,
    Post,
    Image,
}
