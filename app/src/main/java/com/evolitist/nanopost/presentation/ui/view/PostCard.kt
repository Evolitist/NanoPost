package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.evolitist.nanopost.domain.model.Post

@Composable
fun PostCard(
    post: Post,
    onClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onHeaderClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick(post.id) },
    ) {
        PostContent(
            post = post,
            onImageClick = onImageClick,
            onHeaderClick = onHeaderClick,
            isStandalone = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
