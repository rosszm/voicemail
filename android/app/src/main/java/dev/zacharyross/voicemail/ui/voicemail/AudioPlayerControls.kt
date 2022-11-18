package dev.zacharyross.voicemail.ui.voicemail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.Player
import dev.zacharyross.voicemail.ui.voicemail.button.IconButton
import kotlinx.coroutines.delay
import java.time.Duration


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AudioPlayerControls(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    prepare: () -> Unit,
    play: () -> Unit,
    pause: () -> Unit,
    replay: (ms: Long) -> Unit,
    forward: (ms: Long) -> Unit,
    seekTo: (ms: Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MediaSlider(
            currentPosition = currentPosition,
            duration = duration,
            seekTo = seekTo
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = { replay(5000) },
                rippleRadius = 28.dp,
                enabled = currentPosition != 0L
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Rounded.Replay5,
                    tint = if (currentPosition == 0L) {
                        MaterialTheme.colorScheme.primary
                    }
                    else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    if (isPlaying) {
                        pause()
                    } else {
                        prepare()
                        play()
                    }
                },
                rippleRadius = 40.dp
            ) {
                Icon(
                    modifier = Modifier.size(72.dp),
                    imageVector = if (isPlaying) {
                        Icons.Rounded.Pause
                    }
                    else {
                        Icons.Rounded.PlayArrow
                    },
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = { forward(5000) },
                rippleRadius = 28.dp,
                enabled = currentPosition != duration
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Rounded.Forward5,
                    tint = if (currentPosition == duration) {
                        MaterialTheme.colorScheme.primary
                    }
                    else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun MediaSlider(
    currentPosition: Long,
    duration: Long,
    seekTo: (ms: Long) -> Unit,
) {
    var elapsed by remember { mutableStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    val maxDuration by remember { derivedStateOf {
        if (duration != TIME_UNSET) duration.toFloat() else 0f
    } }
    val timeElapsedText by remember { derivedStateOf {
        val current = Duration.ofMillis(elapsed.toLong())
        val mins = current.toMinutesPart()
        val secs = current.toSecondsPart()
        String.format("%d:%s", mins, if (secs < 10) "0$secs" else "$secs")
    }}
    val timeRemainingText by remember { derivedStateOf {
        val current = Duration.ofMillis(maxDuration.toLong())
        val mins = current.toMinutesPart()
        val secs = current.toSecondsPart()
        String.format("%d:%s", mins, if (secs < 10) "0$secs" else "$secs")
    }}

    if (!isSeeking) {
        LaunchedEffect(Unit) {
            while(true) {
                elapsed = currentPosition.toFloat()
                delay(1000)
            }
        }

    }

    Column {
        Slider(
            value = elapsed,
            onValueChange = {
                isSeeking = true
                elapsed = it
            },
            onValueChangeFinished = {
                isSeeking = false
                seekTo(elapsed.toLong())
            },
            valueRange = 0f..maxDuration
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = timeElapsedText, fontSize = 12.sp)
            Text(text = timeRemainingText, fontSize = 12.sp)
        }
    }
}