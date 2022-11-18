package dev.zacharyross.voicemail.data.service

import android.content.ComponentName
import android.content.Context
import androidx.concurrent.futures.await
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken


class PlaybackServiceConnection(private val context: Context) {
    private val sessionToken = SessionToken(
        context,
        ComponentName(context, PlaybackService::class.java)
    )

    suspend fun getPlayer(): Player {
        return MediaController.Builder(context, sessionToken).buildAsync().await()
    }
}