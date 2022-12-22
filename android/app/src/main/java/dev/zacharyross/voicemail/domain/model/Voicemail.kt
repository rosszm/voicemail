package dev.zacharyross.voicemail.domain.model

import java.time.ZonedDateTime


/**
 * The voicemail data class.
 *
 * This class represents a voicemail message.
 */
interface Voicemail {
    val id: String
    val userId: String
    val fromNumber: String
    val audioUrl: String
    val audioDuration: Long
    val transcription: String
    val dateTime: ZonedDateTime
    val unread: Boolean
}