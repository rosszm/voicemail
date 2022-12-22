package dev.zacharyross.voicemail.service

import android.content.ComponentName
import android.content.Context
import androidx.concurrent.futures.await
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken


class PlaybackServiceConnection(private val context: Context) {
    private val sessionToken = SessionToken(
        context,
        ComponentName(context, PlaybackService::class.java)
    )

    suspend fun getMediaController(): MediaController {
        return MediaController.Builder(context, sessionToken).buildAsync().await()
    }
}