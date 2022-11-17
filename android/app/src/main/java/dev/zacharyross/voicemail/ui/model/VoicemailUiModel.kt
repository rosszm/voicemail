package dev.zacharyross.voicemail.ui.model


import dev.zacharyross.voicemail.domain.model.Voicemail
import java.time.ZonedDateTime


/**
 * The voicemail UI model data class.
 *
 * This class represents the data required to display a voicemail in the view.
 */
data class VoicemailUiModel(
    override val id: Int,
    override val toNumber: String,
    override val fromNumber: String,
    override val audioUrl: String,
    override val transcription: String,
    override val dateTime: ZonedDateTime,
    override val unread: Boolean,
    val contact: DisplayContact?,
): Voicemail {
    constructor(voicemail: Voicemail, contact: DisplayContact?):
        this(
            voicemail.id,
            voicemail.toNumber,
            voicemail.fromNumber,
            voicemail.audioUrl,
            voicemail.transcription,
            voicemail.dateTime,
            voicemail.unread,
            contact
        )
}

