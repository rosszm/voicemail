package dev.zacharyross.voicemail.ui.inbox


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


/**
 * The message data class.
 *
 * This class represents the state of a message in the view-model/UI.
 */
data class Voicemail(
    val to: String,
    var from: String,
    val audioUrl: String,
    val transcription: String,
    val dateTime: ZonedDateTime,
    var unread: Boolean)


/**
 * Creates an inbox map from a list of voicemails.
 */
fun buildInbox(voicemails: List<Voicemail>): Map<String, List<Voicemail>> {
    val inbox: MutableMap<String, List<Voicemail>> = mutableMapOf()
    voicemails.sortedByDescending { voicemail -> voicemail.dateTime }
        .forEach { voicemail ->
        val group = if (ChronoUnit.DAYS.between(voicemail.dateTime, ZonedDateTime.now()) < 1)
            "Today"
        else if (ChronoUnit.WEEKS.between(voicemail.dateTime, ZonedDateTime.now()) < 1)
            "Past week"
        else
            "Older"

        val list = inbox.get(group)
        if (list != null) {
            inbox[group] = list + voicemail
        }
        else {
            inbox[group] = listOf(voicemail)
        }
    }
    return inbox.toMap()
}

/**
 * The voicemail inbox composable.
 *
 * T
 */
@Composable
fun VoicemailInbox(inbox: Map<String, List<Voicemail>>) {
    LazyColumn() {
        inbox.forEach { (dateTag, voicemailList) ->
            item {
                Text(
                    text = dateTag,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp)
                )
            }
            items(voicemailList) { voicemail ->
                InboxItem(voicemail)
            }
        }
    }
}



