package dev.zacharyross.voicemail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zacharyross.voicemail.ui.inbox.Voicemail
import dev.zacharyross.voicemail.ui.inbox.VoicemailInbox
import dev.zacharyross.voicemail.ui.inbox.buildInbox
import dev.zacharyross.voicemail.ui.theme.VoicemailTheme
import java.time.ZonedDateTime

/**
 * The voicemail app state.
 *
 * This class represents application-wide state such as navigation.
 */
class VoicemailAppState() {

}

/**
 * The composable voicemail app.
 *
 * This is the root composable for the entire voicemail application.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoicemailApp() {
    val voicemailList = listOf(
        Voicemail(
            "+13061234567",
            "(306) 994-3716",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription. This is a longer message transcription",
            ZonedDateTime.now(),
            true),
        Voicemail(
            "+13061234567",
            "+13068342266",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusDays(1),
            true),
        Voicemail(
            "+13061234567",
            "(306) 994-3716",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusWeeks(2),
            false),
        Voicemail(
            "+13061234567",
            "(306) 994-3716",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(1),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(2),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(3),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(3),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(3),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(3),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(3),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(3),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusMonths(3),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusYears(1),
            false),
        Voicemail(
            "+13061234567",
            "+11231231234",
            "https://www2.cs.uic.edu/~i101/SoundFiles/gettysburg.wav",
            "This is a test message transcription.",
            ZonedDateTime.now().minusYears(2),
            false),
    )

    VoicemailTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold {
                VoicemailInbox(inbox = buildInbox(voicemailList))
            }
        }
    }
}