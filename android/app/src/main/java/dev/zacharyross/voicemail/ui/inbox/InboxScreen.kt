package dev.zacharyross.voicemail.ui.inbox

import android.Manifest
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseUser
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.destinations.InboxScreenDestination
import dev.zacharyross.voicemail.ui.destinations.PhoneAuthScreenDestination
import dev.zacharyross.voicemail.ui.destinations.VoicemailScreenDestination
import dev.zacharyross.voicemail.ui.inbox.topbar.InboxTopSearchBar
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@InboxNavGraph(start = true)
@Destination(style = InboxTransitions::class)
@Composable
fun InboxScreen(
    navigator: DestinationsNavigator,
    viewModel: InboxViewModel,
) {
    val context = LocalContext.current
    var inbox: List<VoicemailUiModel> by remember { mutableStateOf(listOf()) }
    val groupedInbox by remember { derivedStateOf { groupInbox(context, inbox) } }
    var user: FirebaseUser? by remember { mutableStateOf(null) }
    var contactPermissionAlreadyRequested by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (viewModel.inboxFlow == null)
            delay(10)
        viewModel.authStateFlow?.collectLatest {
            user = it
            if (it == null)
                navigator.navigate(PhoneAuthScreenDestination(InboxScreenDestination.route)) {
                    popUpTo(InboxScreenDestination.route) {
                        inclusive = true
                    }
                }
        }
    }

    if (user != null) {
        val contactPermissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS) { granted ->
            contactPermissionAlreadyRequested = true
            if (granted) { viewModel.updateInboxWithContacts() }
        }

        LaunchedEffect(Unit) {
            when (contactPermissionState.status) {
                PermissionStatus.Granted -> {
                    viewModel.updateInboxWithContacts()
                }
                is PermissionStatus.Denied -> {
                    if (!contactPermissionAlreadyRequested)
                        contactPermissionState.launchPermissionRequest()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (viewModel.inboxFlow == null)
            delay(10)
        viewModel.inboxFlow?.collectLatest {
            inbox = it
        }
    }

    AnimatedVisibility(
        visible = user != null,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Scaffold { padding ->
            Box(Modifier.padding(padding).padding(horizontal = 2.dp)) {
                InboxTopSearchBar(
                    modifier = Modifier.zIndex(6f),
                    search = { query -> viewModel.searchVoicemail(query) },
                    onResultClick = { navigator.navigate(VoicemailScreenDestination(it)) },
                    onSignOutClick = { viewModel.signOutUser() }
                )
                LazyColumn {
                    groupedInbox.onEachIndexed { index, (dateTag, voicemails) ->
                        item(key = dateTag) {
                            Text(
                                text = dateTag,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = if (index == 0) 80.dp else 0.dp)
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
                                    viewModel.setVoicemailAsRead(voicemail)
                                    navigator.navigate(VoicemailScreenDestination(voicemail.id))
                                }
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = inbox.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    EmptyInboxView()
                }
            }
        }
    }
}


private fun groupInbox(context: Context, inbox: List<VoicemailUiModel>): Map<String, List<VoicemailUiModel>> {
    val grouped: MutableMap<String, List<VoicemailUiModel>> = mutableMapOf()
    inbox.forEach { voicemail ->
        val group = if (ChronoUnit.DAYS.between(voicemail.dateTime, ZonedDateTime.now()) < 1)
            context.getString(R.string.today)
        else if (ChronoUnit.WEEKS.between(voicemail.dateTime, ZonedDateTime.now()) < 1)
            context.getString(R.string.past_week)
        else
            context.getString(R.string.older)

        val list = grouped[group]
        if (list != null) {
            grouped[group] = list + voicemail
        }
        else {
            grouped[group] = listOf(voicemail)
        }
    }
    return grouped
}

