package dev.zacharyross.voicemail.ui.common.time

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import android.text.format.DateFormat;
import android.text.format.DateFormat.getBestDateTimePattern
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import dev.zacharyross.voicemail.R
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.time.Duration.Companion.minutes


/**
 * A composable element that displays the time or date based on the time since a given date-time.
 *
 * The date/time displayed is updated every 1 minute.
 */
@Composable
fun DateTimeText(
    dateTime: ZonedDateTime,
    color: Color = Color.Black,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val context = LocalContext.current
    var timeSince by remember {
        mutableStateOf(getFormattedDateTimeString(dateTime, context))
    }

    // Effect that updates the displayed datetime text every minute. Only for datetime values that
    // occurred less than 1 day ago; this prevents the job from doing work that does not change the
    // displayed value.
    LaunchedEffect(dateTime) {
        while(ChronoUnit.DAYS.between(dateTime, ZonedDateTime.now()) < 2) {
            timeSince = getFormattedDateTimeString(dateTime, context)
            delay(1.minutes)
        }
    }

    Text(
        text = timeSince,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}


/**
 * Returns a date time string formatted based on the duration of time since the given date-time.
 */
private fun getFormattedDateTimeString(zonedDateTime: ZonedDateTime, context: Context): String {
    val dateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
    return if (ChronoUnit.MINUTES.between(dateTime, ZonedDateTime.now()) < 1) {
        context.getString(R.string.just_now)
    }
    else if (ChronoUnit.HOURS.between(dateTime, ZonedDateTime.now()) < 1) {
        val mins = ChronoUnit.MINUTES.between(dateTime, ZonedDateTime.now())
        context.resources.getQuantityString(R.plurals.minutes_ago_short, mins.toInt(), mins)
    }
    else if (ChronoUnit.DAYS.between(dateTime, ZonedDateTime.now()) < 1
        && dateTime.dayOfMonth == ZonedDateTime.now().dayOfMonth
    ) {
        if (DateFormat.is24HourFormat(context)) {
            dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        else {
            dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        }
    }
    else if (ChronoUnit.DAYS.between(dateTime, ZonedDateTime.now()) < 2) {
        context.getString(R.string.yesterday)
    }
    else if (ChronoUnit.WEEKS.between(dateTime, ZonedDateTime.now()) < 1) {
        dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
    else if (ChronoUnit.YEARS.between(dateTime, ZonedDateTime.now()) < 1) {
        dateTime.format(DateTimeFormatter.ofPattern(
            getBestDateTimePattern(Locale.getDefault(),"dd MMM")))
    }
    else {
        if (DateFormat.is24HourFormat(context)) {
            dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
        else {
            dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        }
    }
}