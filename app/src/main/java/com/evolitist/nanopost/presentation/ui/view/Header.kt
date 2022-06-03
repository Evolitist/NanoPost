package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.evolitist.nanopost.domain.model.ProfileCompact

enum class HeaderStyle {
    Profile,
    Post,
}

@Composable
fun Header(
    style: HeaderStyle,
    profile: ProfileCompact,
    subtitle: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val clickHandler by rememberUpdatedState(onClick)
    val profileName = remember(profile) {
       profile.displayName ?: profile.username
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clickable(enabled = onClick != null) {
                clickHandler?.invoke()
            },
    ) {
        Avatar(
            model = profile.avatarUrl,
            monogram = {
                Text(
                    text = profileName.take(1).uppercase(),
                    style = style.monogramTextStyle(),
                )
            },
            modifier = Modifier.size(style.avatarSize()),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(style.textSpacing()),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = profileName,
                style = style.titleTextStyle(),
            )
            ProvideTextStyle(style.subtitleTextStyle()) {
                subtitle()
            }
        }
    }
}

@Composable
fun Avatar(
    model: Any?,
    monogram: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
    ) {
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.surface,
            ) {
                monogram()
            }
        }
    }
}

private fun HeaderStyle.avatarSize() = when (this) {
    HeaderStyle.Profile -> 64.dp
    HeaderStyle.Post -> 40.dp
}

private fun HeaderStyle.textSpacing() = when (this) {
    HeaderStyle.Profile -> 4.dp
    HeaderStyle.Post -> 0.dp
}

@Composable
@ReadOnlyComposable
private fun HeaderStyle.monogramTextStyle() = when (this) {
    HeaderStyle.Profile -> MaterialTheme.typography.headlineLarge
    HeaderStyle.Post -> MaterialTheme.typography.titleMedium
}

@Composable
@ReadOnlyComposable
private fun HeaderStyle.titleTextStyle() = when (this) {
    HeaderStyle.Profile -> MaterialTheme.typography.titleLarge
    HeaderStyle.Post -> MaterialTheme.typography.labelLarge
}

@Composable
@ReadOnlyComposable
private fun HeaderStyle.subtitleTextStyle() = when (this) {
    HeaderStyle.Profile -> MaterialTheme.typography.bodySmall
    HeaderStyle.Post -> MaterialTheme.typography.bodySmall
}
