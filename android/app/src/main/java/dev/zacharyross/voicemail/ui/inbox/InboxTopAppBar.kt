package dev.zacharyross.voicemail.ui.inbox

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.smallTopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.zacharyross.voicemail.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxTopAppBar(
    modifier: Modifier = Modifier,
    isFixed: Boolean?,
    scrollBehavior: TopAppBarScrollBehavior?
) {
    var showMoreMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        scrollBehavior = if (isFixed == true) TopAppBarDefaults.pinnedScrollBehavior() else scrollBehavior,
        colors = smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .padding(start = 8.dp)
                            )
                            Text(
                                text = "Search messages",
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.padding(4.dp)
                            )
                        }
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
                                    text = { Text(text = "Settings")},
                                    onClick = { /*TODO*/ })
                                DropdownMenuItem(
                                    text = { Text(text = "Help & feedback")},
                                    onClick = { /*TODO*/ })
                            }
                        }
                    }
                }
        },
    )
}