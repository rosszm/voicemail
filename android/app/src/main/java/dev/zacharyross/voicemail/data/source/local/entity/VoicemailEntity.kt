package dev.zacharyross.voicemail.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import dev.zacharyross.voicemail.domain.model.Voicemail


@Entity(tableName = "Voicemail")
data class VoicemailEntity(
    @PrimaryKey override val id: String,
    @ColumnInfo(name = "user_id") override val userId: String,
    @ColumnInfo(name = "from_number") override val fromNumber: String,
    @ColumnInfo(name = "audio_url") override val audioUrl: String,
    @ColumnInfo(name = "audio_duration") override val audioDuration: Long,
    @ColumnInfo(name = "transcription") override val transcription: String,
    @ColumnInfo(name = "date_time") override val dateTime: ZonedDateTime,
    @ColumnInfo(name = "unread") override val unread: Boolean,
    @ColumnInfo(name = "is_deleted") val deleted: Boolean = false,
): Voicemail {
    constructor(voicemail: Voicemail):
        this(
            id = voicemail.id,
            userId = voicemail.userId,
            fromNumber = voicemail.fromNumber,
            audioUrl = voicemail.audioUrl,
            audioDuration = voicemail.audioDuration,
            transcription = voicemail.transcription,
            dateTime = voicemail.dateTime,
            unread = voicemail.unread,
            deleted = false,
        )
}
