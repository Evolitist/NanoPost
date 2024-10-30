package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Counter(
    count: Int,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count.shorten(),
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
    }
}

private fun Int.shorten() = when {
    this >= 1_000_000_000 -> this.div(1_000_000_000F).toString()
        .take(4).trimZeroes().plus("B")
    this >= 100_000_000 -> this.div(100_000_000F).toString()
        .take(3).trimZeroes().plus("M")
    this >= 10_000_000 -> this.div(10_000_000F).toString()
        .take(4).trimZeroes().plus("M")
    this >= 1_000_000 -> this.div(1_000_000F).toString()
        .take(4).trimZeroes().plus("M")
    this >= 100_000 -> this.div(100_000F).toString()
        .take(3).trimZeroes().plus("K")
    this >= 10_000 -> this.div(10_000F).toString()
        .take(4).trimZeroes().plus("K")
    this >= 1_000 -> this.div(1_000F).toString()
        .take(4).trimZeroes().plus("K")
    else -> this.toString()
}

private fun String.trimZeroes() = trimEnd { it == '0' || it == '.' }
