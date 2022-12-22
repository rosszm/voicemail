package dev.zacharyross.voicemail.ui.inbox.voicemail

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
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import dev.zacharyross.voicemail.ui.inbox.voicemail.button.IconButton
import kotlinx.coroutines.delay
import java.time.Duration


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AudioPlayerControls(
    player: Player?,
    duration: Long?,
) {
    var isPlaying by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    var isEnded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = object: Player.Listener {
            override fun onIsPlayingChanged(value: Boolean) {
                isPlaying = value
            }
            override fun onTracksChanged(tracks: Tracks) {
                println(player?.duration)
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                isEnded = playbackState == Player.STATE_ENDED
            }
        }
        player?.addListener(listener)

        onDispose {
            player?.removeListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            isStarted = player?.currentPosition != 0L
            delay(100)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MediaSlider(player = player, duration = duration)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = { player?.seekBack() },
                rippleRadius = 28.dp,
                enabled = isStarted
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Rounded.Replay5,
                    tint = if (isStarted) {
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
                        player?.pause()
                    } else {
                        if (isEnded) {
                            player?.seekTo(0L)
                        }
                        player?.prepare()
                        player?.play()
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
                onClick = { player?.seekForward() },
                rippleRadius = 28.dp,
                enabled = !isEnded
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Rounded.Forward5,
                    tint = if (!isEnded) {
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
fun MediaSlider(player: Player?, duration: Long?) {
    var sliderPosition by remember { mutableStateOf(0L) }
    var sliderDuration by remember { mutableStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }

    val timeElapsedText by remember { derivedStateOf {
        formatTimeString(sliderPosition) } }

    val timeRemainingText by remember { derivedStateOf {
        "-" + formatTimeString((sliderDuration - sliderPosition).coerceAtLeast(0)) } }

    LaunchedEffect(duration) {
        if (duration != null) {
            sliderDuration = duration
        }
    }

    if (!isSeeking) {
        LaunchedEffect(Unit) {
            while (true) {
                val pos = player?.currentPosition ?: 0
                sliderPosition = pos
                delay(300)
            }
        }
    }

    Column {
        Slider(
            value = sliderPosition.toFloat(),
            onValueChange = {
                isSeeking = true
                sliderPosition = it.toLong()
            },
            onValueChangeFinished = {
                isSeeking = false
                player?.seekTo(sliderPosition)
            },
            valueRange = 0f..sliderDuration.toFloat()
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


/**
 * Returns a string representation of a time given in milliseconds.
 */
fun formatTimeString(millis: Long): String {
    val duration = Duration.ofMillis(millis)
    return String.format("%01d:%02d", duration.toMinutesPart(), duration.toSecondsPart())
}