package dev.zacharyross.voicemail.ui.inbox

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.zacharyross.voicemail.ui.destinations.VoicemailScreenDestination


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@RootNavGraph(start = true)
@Destination(style = InboxTransitions::class)
@Composable
fun InboxScreen(
    navigator: DestinationsNavigator,
    inboxViewModel: InboxViewModel = hiltViewModel()
) {
    var appBarIsFixed by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var contactPermissionAlreadyRequested by rememberSaveable { mutableStateOf(false) }
    val contactPermissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS) { granted ->
        contactPermissionAlreadyRequested = true
        if (granted)
            inboxViewModel.updateInboxWithContacts()
    }

    LaunchedEffect(true) {
        when (contactPermissionState.status) {
            PermissionStatus.Granted -> {
                inboxViewModel.updateInboxWithContacts()
            }
            is PermissionStatus.Denied -> {
                if (!contactPermissionAlreadyRequested)
                    contactPermissionState.launchPermissionRequest()
            }
        }
    }

    Scaffold(
        topBar = { InboxTopAppBar(isFixed = appBarIsFixed, scrollBehavior = scrollBehavior) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            inboxViewModel.groupedInbox.forEach { (dateTag, voicemails) ->
                item(key = dateTag) {
                    Text(
                        text = dateTag,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 18.dp)
                    )
                }
                items(
                    items = voicemails,
                    key = { voicemail -> voicemail.id }
                ) { voicemail ->
                    InboxItem(
                        voicemail = voicemail,
                        onClick = {
                            inboxViewModel.setVoicemailAsRead(voicemail)
                            navigator.navigate(VoicemailScreenDestination(voicemail.id))
                        }
                    )
                }
            }
        }
        if (inboxViewModel.inboxIsEmpty()) {
            appBarIsFixed = true
            EmptyInboxView()
        }
        else {
            appBarIsFixed = false
        }
    }
}