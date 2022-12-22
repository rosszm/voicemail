package dev.zacharyross.voicemail.domain.repository

interface NotificationRepository {

    /**
     * Add a new notification associated with a given message. Returns the notificationId
     */
    fun addNotification(messageId: String): Int

    /**
     * Removes a notification associated with a given message.
     */
    fun removeNotification(messageId: String)

    /**
     * Returns the message ID from notification ID, or null if not exists.
     */
    fun getMessageId(notificationId: Int): String?

    /**
     * Returns the notification ID from message ID, or null if not exists.
     */
    fun getNotificationId(messageId: String): Int?
}