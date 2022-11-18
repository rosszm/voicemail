package dev.zacharyross.voicemail.ui.inbox


import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.zacharyross.voicemail.ui.model.DisplayContact
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.time.DateTimeText


@Composable
fun InboxItem(
    voicemail: VoicemailUiModel,
    onClick: () -> Unit,
) {
    val displayName by remember { derivedStateOf {
        if (voicemail.contact != null) {
            voicemail.contact.displayName
        }
        else PhoneNumberUtils.formatNumber(voicemail.fromNumber, "1")
    } }
    val fontWeight by remember { derivedStateOf {
        if (voicemail.unread) FontWeight.Bold else FontWeight.Normal
    } }


    Box(modifier = Modifier // Clickable area
        .clickable { onClick() }
    ) {
        Row() {
            Column(
                Modifier.padding(vertical = 18.dp, horizontal = 18.dp)
            ) {
                Row( // Title row
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = displayName,
                        fontWeight = fontWeight,
                        fontSize = 16.sp
                    )
                    DateTimeText(
                        dateTime = voicemail.dateTime,
                        color = if (voicemail.unread) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = fontWeight
                    )
                }
                Row(
                    // info row
                    Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = voicemail.transcription,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = fontWeight,
                        color = if (voicemail.unread) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}