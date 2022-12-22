package dev.zacharyross.voicemail.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

@Composable
fun Illustration(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    tintColor: Color = Color.White,
    alpha: Float = 1f,
) {
    Image(
        painter = painterResource(id),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tintColor.copy(alpha = alpha), BlendMode.Modulate),
        modifier = modifier
    )
}