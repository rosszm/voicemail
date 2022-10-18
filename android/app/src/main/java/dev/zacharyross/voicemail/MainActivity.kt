package dev.zacharyross.voicemail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * The main activity of the voicemail application.
 *
 * This activity acts as an entrypoint to the compose application.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VoicemailApp() }
    }
}

