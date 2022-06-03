package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.evolitist.nanopost.domain.model.Profile

@Composable
fun ProfileCard(
    profile: Profile,
    buttonText: String,
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
                subtitle = { Text(profile.bio ?: "<empty>") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )

            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth().height(80.dp),
            ) {
                Counter(
                    count = profile.subscribersCount,
                    description = "subscribers",
                    modifier = Modifier.fillMaxHeight().weight(1f),
                )
                Counter(
                    count = profile.imagesCount,
                    description = "images",
                    modifier = Modifier.fillMaxHeight().weight(1f),
                )
                Counter(
                    count = profile.postsCount,
                    description = "posts",
                    modifier = Modifier.fillMaxHeight().weight(1f),
                )
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            FilledTonalButton(
                onClick = onButtonClick,
                content = { Text(buttonText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}
