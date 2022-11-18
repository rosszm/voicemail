package dev.zacharyross.voicemail.ui.voicemail


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacharyross.voicemail.data.service.PlaybackServiceConnection
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import dev.zacharyross.voicemail.ui.destinations.VoicemailScreenDestination
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.util.getContactInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class VoicemailViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: VoicemailRepository,
    private val playbackServiceConnection: PlaybackServiceConnection,
    private val state: SavedStateHandle
): ViewModel() {
    var voicemail: VoicemailUiModel? = null
        private set

    var player: Player? = null
        private set

    var playerPositionFlow: StateFlow<Long>? = null
    private set

    var duration: MutableState<Long> = mutableStateOf(0L)

    init {
        viewModelScope.launch {
            val id: Int? = state[VoicemailScreenDestination.NavArgs::id.name]
            voicemail = if (id != null) {
                val vm = repository.getVoicemail(id)
                val contact = getContactInfo(appContext.contentResolver, vm.fromNumber)
                VoicemailUiModel(vm, contact)
            }
            else null

            player = playbackServiceConnection.getPlayer()
            if (voicemail != null) {
                player?.setMediaItem(
                    MediaItem.Builder().setMediaId(voicemail!!.audioUrl).build()
                )
                duration.value = player?.duration ?: 0L
            }

            playerPositionFlow = flow {
                emit(player?.currentPosition ?: 0L)
            }.stateIn(viewModelScope)
        }
    }

    fun isLoading(): Boolean {
        return voicemail == null || player == null
    }

    fun deleteVoicemail(voicemail: VoicemailUiModel) {
        viewModelScope.launch {
            repository.deleteVoicemail(voicemail)
        }
    }

    override fun onCleared() {
        player?.stop()
        super.onCleared()
    }
}