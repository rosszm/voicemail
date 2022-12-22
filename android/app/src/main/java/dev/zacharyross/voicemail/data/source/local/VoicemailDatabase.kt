package dev.zacharyross.voicemail.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.zacharyross.voicemail.data.source.local.dao.VoicemailDao
import dev.zacharyross.voicemail.data.source.local.entity.VoicemailEntity
import dev.zacharyross.voicemail.data.source.local.util.ZonedDateTimeConverter


@Database(entities = [VoicemailEntity::class], version = 3, exportSchema = false)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class VoicemailDatabase : RoomDatabase() {
    abstract fun voicemailDao(): VoicemailDao
}
