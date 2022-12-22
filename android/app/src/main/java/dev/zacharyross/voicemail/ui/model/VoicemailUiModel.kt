package dev.zacharyross.voicemail.ui.model


import dev.zacharyross.voicemail.domain.model.Voicemail
import java.time.ZonedDateTime


/**
 * The voicemail UI model data class.
 *
 * This class represents the data required to display a voicemail in the view.
 */
data class VoicemailUiModel(
    override val id: String,
    override val userId: String,
    override val fromNumber: String,
    override val audioUrl: String,
    override val audioDuration: Long,
    override val transcription: String,
    override val dateTime: ZonedDateTime,
    override val unread: Boolean,
    val contact: DisplayContact?,
): Voicemail {
    constructor(voicemail: Voicemail, contact: DisplayContact?):
        this(
            voicemail.id,
            voicemail.userId,
            voicemail.fromNumber,
            voicemail.audioUrl,
            voicemail.audioDuration,
            voicemail.transcription,
            voicemail.dateTime,
            voicemail.unread,
            contact
        )
}

