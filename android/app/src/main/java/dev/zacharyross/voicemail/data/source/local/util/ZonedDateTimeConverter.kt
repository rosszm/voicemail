package dev.zacharyross.voicemail.data.source.local.util

import androidx.room.TypeConverter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeConverter {
    /**
     * Converts an ISO offset formatted string in UTC into a `ZonedDateTime` instance.
     */
    @TypeConverter
    fun toZonedDateTime(string: String): ZonedDateTime {
        return ZonedDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    /**
     * Converts a `ZonedDateTime` instance into an ISO offset formatted string in UTC.
     */
    @TypeConverter
    fun toUtcDateTimeString(dateTime: ZonedDateTime): String {
        return dateTime.withZoneSameInstant(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}