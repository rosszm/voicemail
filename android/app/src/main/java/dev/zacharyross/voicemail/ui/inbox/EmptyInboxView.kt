package dev.zacharyross.voicemail.ui.inbox

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Voicemail
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.common.Illustration


@Composable
fun EmptyInboxView() {
    Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val illustrationColor = if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondaryContainer

        Illustration(
            modifier = Modifier
                .fillMaxHeight(0.33f)
                .padding(horizontal = 32.dp, vertical = 8.dp),
            id = R.drawable.illustration_waiting,
            tintColor = illustrationColor,
            alpha = 0.5f
        )
        Text(
            text = stringResource(R.string.inbox_empty),
            color = MaterialTheme.colorScheme.outline,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 64.dp)
        )
    }
}