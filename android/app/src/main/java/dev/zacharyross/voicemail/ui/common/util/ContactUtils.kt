package dev.zacharyross.voicemail.ui.common.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import dev.zacharyross.voicemail.ui.model.DisplayContact


fun getContactInfo(resolver: ContentResolver, phoneNumber: String): DisplayContact? {
    val uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        Uri.encode(phoneNumber))
    val cur = resolver.query(uri, arrayOf(
        ContactsContract.PhoneLookup.DISPLAY_NAME,
        ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
    ), null, null)
    var res: DisplayContact? = null
    if(cur != null) {
        if (cur.moveToFirst()) {
            val photoUriString = cur.getString(1)
            val photoUri = if (photoUriString != null) Uri.parse(photoUriString) else null
            res = DisplayContact(
                displayName = cur.getString(0),
                photoUri = photoUri
            )
        }
        cur.close()
    }
    return res
}