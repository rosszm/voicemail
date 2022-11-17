package dev.zacharyross.voicemail.ui.voicemail


import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacharyross.voicemail.data.service.PlaybackService
import dev.zacharyross.voicemail.domain.model.Voicemail
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import dev.zacharyross.voicemail.ui.destinations.VoicemailScreenDestination
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.util.getContactInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class VoicemailViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: VoicemailRepository,
    private val state: SavedStateHandle
): ViewModel() {
    var voicemail: VoicemailUiModel? = null
        private set

    var player: Player? = null
        private set

    init {
        val sessionToken = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()

        val job = CoroutineScope(Dispatchers.IO).launch {
            val id: Int? = state[VoicemailScreenDestination.NavArgs::id.name]
            voicemail = if (id != null) {
                val vm = repository.getVoicemail(id)
                val contact = getContactInfo(appContext.contentResolver, vm.fromNumber)
                VoicemailUiModel(vm, contact)
            }
            else null
        }

        controllerFuture.addListener(
            {
                player = controllerFuture.get()
                if (voicemail != null) {
                    val mediaItem = MediaItem.Builder().setMediaId(voicemail!!.audioUrl).build()
                    player?.setMediaItem(mediaItem)
                }
                player?.prepare()
            },
            MoreExecutors.directExecutor()
        )
    }

    fun isLoading(): Boolean {
        return voicemail == null || player == null
    }

    fun deleteVoicemail(voicemail: VoicemailUiModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteVoicemail(voicemail)
        }
    }

    override fun onCleared() {
        player?.stop()
        player = null
        super.onCleared()
    }
}