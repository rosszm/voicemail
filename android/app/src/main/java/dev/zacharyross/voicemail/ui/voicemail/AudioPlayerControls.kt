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
    player: Player?,
    mediaItemId: Int
) {
    var isPlaying by remember { mutableStateOf(false) }
    var isSeeking by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }

    LaunchedEffect(player?.isLoading) {
        while (player == null) delay(100)
        duration = player.duration
    }

    DisposableEffect(true) {
        val listener = object: Player.Listener {
            override fun onIsPlayingChanged(_isPlaying: Boolean) {
                isPlaying = _isPlaying
            }
        }
        player?.addListener(listener)
        onDispose {
            player?.removeListener(listener)
        }
    }

    LaunchedEffect(isPlaying) {
        while (true) {
            if (!isSeeking) {
                currentPosition = player?.currentPosition ?: 0
                delay(1000)
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = {
                isSeeking = true
                currentPosition = it.toLong()
            },
            onValueChangeFinished = {
                isSeeking = false
                player?.seekTo(currentPosition)
            },
            valueRange = 0f..if (player != null && player.duration != TIME_UNSET)
                player.duration.toFloat() else 0f
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val current = Duration.ofMillis(currentPosition)
            var mins = current.toMinutesPart()
            var secs = current.toSecondsPart()
            Text(
                text = String.format("%d:%s", mins, if (secs < 10) "0$secs" else "$secs"),
                fontSize = 12.sp
            )
            val remaining = Duration.ofMillis(duration - currentPosition)
            println(duration)
            mins = remaining.toMinutesPart()
            secs = remaining.toSecondsPart()
            Text(
                text = String.format("-%d:%s", mins, if (secs < 10) "0$secs" else "$secs"),
                fontSize = 12.sp
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = { player?.seekToPreviousMediaItem() },
                rippleRadius = 28.dp,
                enabled = player?.hasPreviousMediaItem() == true
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Rounded.Replay5,
                    tint = if (player?.hasPreviousMediaItem() == true) {
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
                    isPlaying = if (isPlaying) {
                        player?.pause()
                        false
                    } else {
                        player?.play()
                        true
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
                onClick = { player?.seekToNextMediaItem() },
                rippleRadius = 28.dp,
                enabled = player?.hasNextMediaItem() == true
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Rounded.Forward5,
                    tint = if (player?.hasNextMediaItem() == true) {
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