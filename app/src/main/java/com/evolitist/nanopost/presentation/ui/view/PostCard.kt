package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessStarted
import com.evolitist.nanopost.domain.model.Post

@Composable
fun PostCard(
    post: Post,
    onClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onHeaderClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onClickState by rememberUpdatedState { onClick(post.id) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = dropUnlessStarted { onClickState() },
    ) {
        PostContent(
            post = post,
            onImageClick = onImageClick,
            onHeaderClick = onHeaderClick,
            isStandalone = false,
        )
    }
}
