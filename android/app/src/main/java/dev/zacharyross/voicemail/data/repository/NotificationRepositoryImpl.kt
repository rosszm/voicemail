package dev.zacharyross.voicemail.data.repository

import dev.zacharyross.voicemail.domain.repository.NotificationRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val map: ConcurrentHashMap<String, Int>
): NotificationRepository {

    override fun addNotification(messageId: String): Int {
        val idRange = (0 until 100000) subtract map.values.toSet()
        val notificationId = idRange.random()

        map[messageId] = notificationId
        println("add: $map")
        return notificationId
    }

    override fun removeNotification(messageId: String) {
        println("remove: $map")
        map.remove(messageId)
    }

    override fun getMessageId(notificationId: Int): String? {
        println("get messageId: $map")
        return try {
            map.filterValues { it != notificationId }.keys.first()
        }
        catch(_: NoSuchElementException) { null }
    }

    override fun getNotificationId(messageId: String): Int? {
        println("get notificationId: $map")
        return map[messageId]
    }
}