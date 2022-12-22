package dev.zacharyross.voicemail.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.core.net.toUri
import androidx.work.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dev.zacharyross.voicemail.APP_URL
import dev.zacharyross.voicemail.MainActivity
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.data.repository.NotificationRepositoryImpl
import dev.zacharyross.voicemail.ui.common.util.getContactInfo
import dev.zacharyross.voicemail.ui.destinations.VoicemailScreenDestination
import dev.zacharyross.voicemail.worker.DataSourceSyncWorker
import dev.zacharyross.voicemail.worker.TokenRefreshWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class MessagingService: FirebaseMessagingService() {
    @Inject lateinit var channel: NotificationChannel
    @Inject lateinit var mapping: NotificationRepositoryImpl

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val work = PeriodicWorkRequestBuilder<TokenRefreshWorker>(30, TimeUnit.DAYS).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                getString(R.string.work_token_refresh),
                ExistingPeriodicWorkPolicy.REPLACE,
                work
            )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(this@MessagingService::class.simpleName, "Message received: ${message.data}")

        val work = OneTimeWorkRequestBuilder<DataSourceSyncWorker>().build()
        val workResult = WorkManager.getInstance(this).enqueueUniqueWork(
            getString(R.string.work_sync_data),
            ExistingWorkPolicy.REPLACE,
            work
        )

        val messageId = message.data["msg_id"] as String
        val notificationId = mapping.addNotification(messageId)

        CoroutineScope(Dispatchers.Default).launch {
            workResult.await()
            val notification = buildNotification(
                id = messageId,
                fromNumber = message.data["from_number"] as String,
                transcription = message.data["transcription"] as String
            )
            with (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
                notify(notificationId, notification)
            }
        }
    }

    /**
     * Builds a new voicemail notification.
     *
     * @param id the message id.
     * @param fromNumber the message sender's phone number
     * @param transcription the message transcription
     */
    private fun buildNotification(id: String, fromNumber: String, transcription: String): Notification {
        val messageUri = "$APP_URL/${VoicemailScreenDestination(id).route}".toUri()

        val intent = Intent(Intent.ACTION_VIEW, messageUri, applicationContext, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return Notification.Builder(this, channel.id)
            .setSmallIcon(R.drawable.ic_voicemail)
            .setContentTitle(formatSender(fromNumber))
            .setContentText(formatTranscription(transcription))
            .setShowWhen(true)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .build()
    }

    /**
     * Formats a phone number into a sender string. Returns the name of the contact if there is a
     * contact for the number; otherwise returns the number in the local format.
     */
    private fun formatSender(fromNumber: String): String {
        return getContactInfo(this.contentResolver, fromNumber)?.displayName
            ?: PhoneNumberUtils.formatNumber(fromNumber, Locale.getDefault().country)
    }

    /**
     * Formats a transcription message. Replaces a blank message stating that there is no
     * transcription available.
     */
    private fun formatTranscription(transcription: String): String {
        return transcription.ifBlank { getString(R.string.no_transcription_available) }
    }
}