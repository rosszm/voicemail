package dev.zacharyross.voicemail.ui.time

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale


/**
 *
 */
@Composable
fun DateText(
    dateTime: ZonedDateTime,
    color: Color = Color.Black,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text =
            if (ChronoUnit.MINUTES.between(dateTime, ZonedDateTime.now()) < 1) {
                "Just now"
            }
            else if (ChronoUnit.HOURS.between(dateTime, ZonedDateTime.now()) < 1) {
                val mins = ChronoUnit.MINUTES.between(dateTime, ZonedDateTime.now())
                val minsStr = if (mins <= 1.toLong()) "min" else "mins"
                "$mins $minsStr ago"
            }
            else if (ChronoUnit.DAYS.between(dateTime, ZonedDateTime.now()) < 2) {
                "Yesterday"
            }
            else if (ChronoUnit.WEEKS.between(dateTime, ZonedDateTime.now()) < 1) {
                dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }
            else if (ChronoUnit.YEARS.between(dateTime, ZonedDateTime.now()) < 1) {
                dateTime.format(DateTimeFormatter.ofPattern("dd MMM"))
            }
            else {
                dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            },
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}