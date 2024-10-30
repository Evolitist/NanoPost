package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Profile

enum class ProfileButtonState {
    Edit, Subscribe, Unsubscribe
}

@Composable
fun ProfileCard(
    profile: Profile,
    buttonState: ProfileButtonState,
    onSubscribersClick: () -> Unit,
    onImagesClick: () -> Unit,
    onPostsClick: () -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Header(
                style = HeaderStyle.Profile,
                profile = profile.compact(),
                subtitle = profile.bio ?: stringResource(R.string.profile_bio_empty),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
            ) {
                Counter(
                    count = profile.subscribersCount,
                    description = pluralStringResource(
                        R.plurals.profile_label_subscribers,
                        profile.subscribersCount,
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(onClick = onSubscribersClick),
                )
                Counter(
                    count = profile.imagesCount,
                    description = pluralStringResource(
                        R.plurals.profile_label_images,
                        profile.imagesCount,
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(onClick = onImagesClick),
                )
                Counter(
                    count = profile.postsCount,
                    description = pluralStringResource(
                        R.plurals.profile_label_posts,
                        profile.postsCount,
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(onClick = onPostsClick),
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            val buttonModifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            when (buttonState) {
                ProfileButtonState.Edit -> FilledTonalButton(
                    onClick = onButtonClick,
                    content = { Text(stringResource(R.string.profile_action_edit)) },
                    modifier = buttonModifier,
                )
                ProfileButtonState.Subscribe -> Button(
                    onClick = onButtonClick,
                    content = { Text(stringResource(R.string.profile_action_subscribe)) },
                    modifier = buttonModifier,
                )
                ProfileButtonState.Unsubscribe -> OutlinedButton(
                    onClick = onButtonClick,
                    content = { Text(stringResource(R.string.profile_action_unsubscribe)) },
                    modifier = buttonModifier,
                )
            }
        }
    }
}
