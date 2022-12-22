package dev.zacharyross.voicemail.ui.inbox.topbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zacharyross.voicemail.R


@Composable
fun SettingsButton(
    onSignOutClick: () -> Unit
) {
    var showMoreMenu by remember { mutableStateOf(false) }

    IconButton(
        modifier = Modifier.padding(0.dp),
        onClick = { showMoreMenu = true }) {
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = stringResource(R.string.more)
        )
        DropdownMenu(
            expanded = showMoreMenu,
            onDismissRequest = { showMoreMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.settings)) },
                onClick = { /*TODO*/ })
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.help_feedback)) },
                onClick = { /*TODO*/ })
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.log_out)) },
                onClick = onSignOutClick)
        }
    }
}

