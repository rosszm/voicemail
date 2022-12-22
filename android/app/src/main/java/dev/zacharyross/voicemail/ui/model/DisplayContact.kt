package dev.zacharyross.voicemail.ui.model

import android.net.Uri


data class DisplayContact(
    val displayName: String,
    val photoUri: Uri?,
)