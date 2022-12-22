package dev.zacharyross.voicemail.domain.usecase

import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacharyross.voicemail.domain.model.Voicemail
import dev.zacharyross.voicemail.domain.repository.NotificationRepository
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SetAsReadUseCase @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val voicemailRepository: VoicemailRepository,
    private val notificationRepository: NotificationRepository
): UseCase<Voicemail, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: Voicemail) {
        voicemailRepository.setVoicemailAsRead(parameters)
        with (appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            val notificationId = notificationRepository.getNotificationId(parameters.id)
            if (notificationId != null) {
                this.cancel(notificationId)
                notificationRepository.removeNotification(parameters.id)
            }
        }
    }
}