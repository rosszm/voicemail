package dev.zacharyross.voicemail.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun BackIcon(
    color: Color? = null,
    description: String? = null
) {
    Icon(
        imageVector = Icons.Rounded.ArrowBack,
        contentDescription = description,
        tint = color ?: MaterialTheme.colorScheme.onSurface)
}

@Composable
fun ErrorIcon(
    color: Color? = null,
    description: String? = null
) {
    Icon(
        imageVector = Icons.Rounded.Error,
        contentDescription = description,
        tint = color ?: MaterialTheme.colorScheme.error)
}

@Composable
fun SearchIcon() {
    Icon(
        imageVector = Icons.Outlined.Search,
        contentDescription = null,
        modifier = Modifier
            .padding(8.dp)
            .padding(start = 8.dp)
    )
}