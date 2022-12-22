package dev.zacharyross.voicemail.ui.inbox.voicemail

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.zacharyross.voicemail.APP_URL
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.inbox.InboxNavGraph
import dev.zacharyross.voicemail.ui.inbox.InboxViewModel
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.common.time.DateTimeText
import dev.zacharyross.voicemail.ui.inbox.voicemail.button.CallBackButton
import dev.zacharyross.voicemail.ui.inbox.voicemail.button.SendTextButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.*


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@InboxNavGraph
@Destination(
    style = VoicemailTransitions::class,
    deepLinks = [
        DeepLink(uriPattern = "$APP_URL/$FULL_ROUTE_PLACEHOLDER")
    ]
)
@Composable
fun VoicemailScreen(
    navigator: DestinationsNavigator,
    viewModel: InboxViewModel,
    id: String
) {
    val scope = rememberCoroutineScope()
    var voicemail: VoicemailUiModel? by remember { mutableStateOf(null) }

    val fromNumberText by remember { derivedStateOf {
        val vm = voicemail
        if (vm != null)
            PhoneNumberUtils.formatNumber(vm.fromNumber, Locale.getDefault().country)
        else
            ""
    } }
    val titleText by remember { derivedStateOf { voicemail?.contact?.displayName ?: fromNumberText } }

    LaunchedEffect(voicemail) {
        val vm = voicemail
        if (vm != null) viewModel.setVoicemailAsRead(vm)
    }

    LaunchedEffect(Unit) {
        while(viewModel.inboxFlow == null)
            delay(10)
        viewModel.inboxFlow?.collectLatest { inbox ->
            voicemail = inbox.find { it.id == id }
        }
    }

    DisposableEffect(Unit) {
        val job = scope.launch {
            while (voicemail == null) {
                delay(100)
            }
            viewModel.player?.setMediaItem(MediaItem.Builder().setMediaId(voicemail!!.audioUrl).build())
        }
        onDispose {
            job.cancel()
            viewModel.player?.pause()
            viewModel.player?.stop()
            viewModel.player?.removeMediaItem(0)
        }
    }

    Scaffold(
        topBar = {
            VoicemailTopAppBar(
                fromNumber = voicemail?.fromNumber ?: "",
                displayContact = voicemail?.contact,
                navigateBack = { navigator.popBackStack() },
                delete = {
                    navigator.popBackStack()
                    viewModel.deleteVoicemail(voicemail)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) { // voicemail details
                        Text(
                            text = titleText,
                            fontSize = 20.sp
                        )
                        DateTimeText(
                            dateTime = voicemail?.dateTime ?: ZonedDateTime.now(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = if (voicemail?.contact != null) fromNumberText else "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
                AudioPlayerControls(viewModel.player, duration = voicemail?.audioDuration)
                Column {
                    Text(
                        text = stringResource(R.string.transcription),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp),
                        fontSize = 18.sp
                    )
                    Text(
                        text = voicemail?.transcription?.ifBlank {
                            stringResource(R.string.no_transcription_available) }
                            ?: stringResource(R.string.no_transcription_available),
                        fontStyle = if (voicemail?.transcription?.isBlank() != false) FontStyle.Italic else FontStyle.Normal
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SendTextButton(voicemail = voicemail)
                CallBackButton(voicemail = voicemail)
            }
        }
    }
}
