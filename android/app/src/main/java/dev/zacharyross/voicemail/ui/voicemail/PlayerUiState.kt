package dev.zacharyross.voicemail.ui.voicemail

data class PlayerUiState(
    val currentPosition: Long,
    val duration: Long,
    val isPlaying: Boolean,
    val isSeeking: Boolean,
)
