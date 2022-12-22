package dev.zacharyross.voicemail.ui.inbox


import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.work.*
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.service.PlaybackServiceConnection
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import dev.zacharyross.voicemail.domain.usecase.SetAsReadUseCase
import dev.zacharyross.voicemail.domain.usecase.auth.MonitorAuthUseCase
import dev.zacharyross.voicemail.domain.usecase.auth.SignOutUseCase
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.common.util.getContactInfo
import dev.zacharyross.voicemail.worker.DataSourceSyncWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class InboxViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val voicemailRepository: VoicemailRepository,
    private val signOutUseCase: SignOutUseCase,
    private val setAsReadUseCase: SetAsReadUseCase,
    private val monitorAuthUseCase: MonitorAuthUseCase,
    private val playbackServiceConnection: PlaybackServiceConnection,
): ViewModel() {
    var inboxFlow: StateFlow<List<VoicemailUiModel>>? = null
        private set

    var authStateFlow: StateFlow<FirebaseUser?>? = null
        private set

    var player: Player? = null
        private set

    init {
        viewModelScope.launch {
            authStateFlow = monitorAuthUseCase.invoke(Unit).getOrNull()?.stateIn(viewModelScope)

            val uid = authStateFlow?.value?.uid ?: ""
            inboxFlow = voicemailRepository.getInbox(uid).map {
                it.map { voicemail ->
                    val contact = getContactInfo(appContext.contentResolver, voicemail.fromNumber)
                    VoicemailUiModel(voicemail, contact)
                }.sortedByDescending { voicemail -> voicemail.dateTime }
            }.stateIn(viewModelScope)

            val work = OneTimeWorkRequestBuilder<DataSourceSyncWorker>().build()
            WorkManager.getInstance(appContext).enqueueUniqueWork(
                appContext.getString(R.string.work_sync_data),
                ExistingWorkPolicy.KEEP,
                work
            )

            player = playbackServiceConnection.getMediaController()
        }
    }

    fun updateInboxWithContacts() {
        viewModelScope.launch {
            inboxFlow = inboxFlow?.map {
                it.map { voicemail ->
                    val contact = getContactInfo(appContext.contentResolver, voicemail.fromNumber)
                    VoicemailUiModel(voicemail, contact)
                }
            }?.stateIn(viewModelScope)
        }
    }

    fun signOutUser() = viewModelScope.launch { signOutUseCase.invoke(Unit) }

    fun setVoicemailAsRead(voicemail: VoicemailUiModel) {
        viewModelScope.launch { setAsReadUseCase.invoke(voicemail) }
    }

    fun deleteVoicemail(voicemail: VoicemailUiModel?) {
        if (voicemail != null)
            viewModelScope.launch {
                val uid = authStateFlow?.value?.uid ?: ""
                voicemailRepository.deleteVoicemails(uid, listOf(voicemail.id))
            }
    }

    fun searchVoicemail(query: String): Flow<List<VoicemailUiModel>> {
        val re = Regex(appContext.getString(R.string.alphanumeric))
        val stripped = re.replace(query, "").lowercase()

        return inboxFlow?.map { inbox ->
            if (stripped.isBlank())
                listOf()
            else
                inbox.filter {
                    it.fromNumber.lowercase().contains(stripped)
                            || it.contact?.displayName?.lowercase()?.contains(stripped) == true
                            || it.transcription.lowercase().contains(stripped)
                }
        } ?: emptyFlow()
    }

    override fun onCleared() {
        player?.release()
        super.onCleared()
    }
}

