package dev.zacharyross.voicemail.ui.voicemail

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.model.DisplayContact


data class MenuItem(val text: String, val action: () -> Unit)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoicemailTopAppBar(
    fromNumber: String,
    displayContact: DisplayContact?,
    navigateBack: () -> Unit,
    delete: () -> Unit,
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

    val startShowOrAddContactActivity = {
        val intent = Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
            Uri.parse("tel:$fromNumber")
        )
        context.startActivity(intent)
    }

    val menuItems = listOf(
        if (displayContact != null) {
            MenuItem(stringResource(R.string.view_contact), startShowOrAddContactActivity)
        }
        else {
            MenuItem(stringResource(R.string.add_contact), startShowOrAddContactActivity)
        },
        MenuItem(stringResource(R.string.block_number), action = { showBlockDialog = true })
    )

    if (showBlockDialog) {
        AlertDialog(
            text = { Text(text = "Do you want to block this number?") },
            confirmButton = {
                TextButton(onClick = {showBlockDialog = false}) {
                    Text(text = "Block", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {showBlockDialog = false}) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = { showBlockDialog = false },
        )
    }

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = { navigateBack() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface)
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        actions = {
            IconButton(onClick = { delete() }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.delete),
                    tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = stringResource(id = R.string.more),
                    tint = MaterialTheme.colorScheme.onSurface)

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false}
                ) {
                    menuItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.text) },
                            onClick = { item.action() }
                        )
                    }
                }
            }

        }
    )
}