package dev.zacharyross.voicemail.ui.auth.pages

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.common.Illustration


@Composable
fun AuthPage(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes illustrationId: Int,
    buttonText: String,
    buttonOnClick: () -> Unit,
    buttonEnabled: Boolean,
    content: @Composable () -> Unit,
) {
    val illustrationColor = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Illustration(
                modifier = Modifier.fillMaxHeight(0.25f).padding(horizontal = 32.dp),
                id = illustrationId,
                tintColor = illustrationColor,
                alpha = 0.8f
            )
            Text(
                modifier = Modifier.padding(vertical = 32.dp),
                text = title,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
            content()
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
                .height(52.dp),
            onClick = buttonOnClick,
            enabled = buttonEnabled
        ) {
            Text(text = buttonText)
        }
    }
}