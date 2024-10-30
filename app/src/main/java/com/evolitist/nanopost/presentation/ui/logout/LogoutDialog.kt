package com.evolitist.nanopost.presentation.ui.logout

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.evolitist.nanopost.R

@Composable
fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.logout)) },
        text = { Text(stringResource(R.string.logout_message)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = { Text(stringResource(R.string.action_confirm)) },
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                content = { Text(stringResource(R.string.action_cancel)) },
            )
        },
        modifier = modifier,
    )
}
