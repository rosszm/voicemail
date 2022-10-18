package dev.zacharyross.voicemail.ui.inbox

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.zacharyross.voicemail.ui.time.DateText


/**
 *
 */
data class DisplayContact(
    val displayName: String,
    val photoUri: Uri?,
)


/**
 *
 */
@Composable
fun InboxItem(voicemail: Voicemail) {
    var displayName = PhoneNumberUtils.formatNumber(voicemail.from, "1")
    var displayImage: Bitmap? = null

    val contactInfo = getContactInfo(LocalContext.current.contentResolver, voicemail.from)
    if (contactInfo != null) {
        displayName = contactInfo.displayName
        if (contactInfo.photoUri != null) {
            displayImage = BitmapFactory.decodeFile(contactInfo.photoUri.path)
        }
    }

    Box(modifier = Modifier // Clickable area
        .clickable { voicemail.unread = false }
    ) {
        Row() {
            Column(
                Modifier.padding(vertical = 16.dp, horizontal = 24.dp)
            ) {
                Row( // Title row
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = displayName,
                        fontWeight = if (voicemail.unread) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                        )
                    DateText(
                        dateTime = voicemail.dateTime,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = if (voicemail.unread) FontWeight.Bold else FontWeight.Normal)
                }
                Row(
                    // info row
                    Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = voicemail.transcription,
                        maxLines = 1,

                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}


/**
 *
 */
private fun getContactInfo(resolver: ContentResolver, phoneNumber: String): DisplayContact? {
    val uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        Uri.encode(phoneNumber))
    val cur = resolver.query(uri, arrayOf(
        ContactsContract.PhoneLookup.DISPLAY_NAME,
        ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
    ), null, null)
    var res: DisplayContact? = null
    if(cur != null && cur.moveToFirst()) {
        val displayName = cur.getString(0)
        val photoUriString = cur.getString(1)
        val photoUri = if (photoUriString != null) Uri.parse(photoUriString) else null
        res = DisplayContact(displayName, photoUri)
        cur.close()
    }
    return res
}