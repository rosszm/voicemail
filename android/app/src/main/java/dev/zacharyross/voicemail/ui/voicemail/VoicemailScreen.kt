package dev.zacharyross.voicemail.ui.voicemail

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.domain.model.Voicemail
import dev.zacharyross.voicemail.ui.inbox.InboxViewModel
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.time.DateTimeText
import dev.zacharyross.voicemail.ui.voicemail.button.CallBackButton
import dev.zacharyross.voicemail.ui.voicemail.button.SendTextButton
import kotlinx.coroutines.delay
import java.time.ZonedDateTime


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination(style = VoicemailTransitions::class)
@Composable
fun VoicemailScreen(
    navigator: DestinationsNavigator,
    id: Int
) {
    val viewModel: VoicemailViewModel = hiltViewModel()
    var player: Player? by remember { mutableStateOf(null) }
    var voicemail: VoicemailUiModel? by remember { mutableStateOf(null) }

    LaunchedEffect(true) {
        while (viewModel.isLoading()) {
            delay(3)
        }
        voicemail = viewModel.voicemail
        player = viewModel.player
    }

    if (voicemail != null) {
        Scaffold(
            topBar = {
                VoicemailTopAppBar(
                    fromNumber = voicemail?.fromNumber ?: "",
                    displayContact = voicemail?.contact,
                    navigateBack = {
                        navigator.popBackStack()
                    },
                    delete = {
                        navigator.popBackStack()
                        viewModel.deleteVoicemail(voicemail!!)
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
                                text = voicemail?.contact?.displayName
                                    ?: PhoneNumberUtils.formatNumber(voicemail?.fromNumber, "1"),
                                fontSize = 20.sp
                            )
                            DateTimeText(
                                dateTime = voicemail?.dateTime ?: ZonedDateTime.now(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = if (voicemail?.contact != null)
                                PhoneNumberUtils.formatNumber(voicemail?.fromNumber, "1")
                            else "",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    //println(player)
                    AudioPlayerControls(player = player, mediaItemId = id)
                    Column {
                        Text(
                            text = stringResource(R.string.transcription),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp),
                            fontSize = 18.sp
                        )
                        Text(text = voicemail?.transcription ?: "")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SendTextButton(voicemail = voicemail!!)
                    CallBackButton(voicemail = voicemail!!)
                }
            }
        }
    }
}

