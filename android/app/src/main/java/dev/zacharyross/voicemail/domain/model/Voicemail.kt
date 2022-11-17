package dev.zacharyross.voicemail.domain.model

import java.time.ZonedDateTime

/**
 * The voicemail data class.
 *
 * This class represents a voicemail message.
 */
interface Voicemail {
    val id: Int
    val toNumber: String
    val fromNumber: String
    val audioUrl: String
    val transcription: String
    val dateTime: ZonedDateTime
    val unread: Boolean
}