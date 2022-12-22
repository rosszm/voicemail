package dev.zacharyross.voicemail.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.zacharyross.voicemail.R


@Composable
fun BackButton(
    description: String? = null,
    iconColor: Color? = null,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick
    ) {
        BackIcon(color = iconColor, description = description)
    }
}
