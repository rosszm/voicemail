package dev.zacharyross.voicemail.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import dev.zacharyross.voicemail.domain.model.Voicemail as VoicemailDomainModel


@Entity(tableName = "Voicemail")
data class VoicemailEntity(
    @PrimaryKey override val id: Int,
    @ColumnInfo(name = "to_number") override val toNumber: String,
    @ColumnInfo(name = "from_number") override val fromNumber: String,
    @ColumnInfo(name = "audio_url") override val audioUrl: String,
    @ColumnInfo(name = "transcription") override val transcription: String,
    @ColumnInfo(name = "date_time") override val dateTime: ZonedDateTime,
    @ColumnInfo(name = "unread") override val unread: Boolean,
): VoicemailDomainModel {
    constructor(voicemail: VoicemailDomainModel):
        this(
            voicemail.id,
            voicemail.toNumber,
            voicemail.fromNumber,
            voicemail.audioUrl,
            voicemail.transcription,
            voicemail.dateTime,
            voicemail.unread,
        )
}
