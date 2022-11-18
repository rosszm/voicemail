package dev.zacharyross.voicemail.ui.inbox


import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.util.getContactInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class InboxViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: VoicemailRepository,
): ViewModel() {
    var inboxFlow: StateFlow<Map<String, List<VoicemailUiModel>>>? = null
        private set

    var currentVoicemail: VoicemailUiModel? = null

    init {
        viewModelScope.launch {
            inboxFlow = repository.getInbox().map {
                groupInbox(

                    it.map { voicemail ->
                        val contact = getContactInfo(appContext.contentResolver, voicemail.fromNumber)
                        VoicemailUiModel(voicemail, contact)
                    }
                )
            }.stateIn(viewModelScope)
        }
    }

    private fun groupInbox(inbox: List<VoicemailUiModel>): Map<String, List<VoicemailUiModel>> {
        val grouped: MutableMap<String, List<VoicemailUiModel>> = mutableMapOf()
        inbox.forEach { voicemail ->
                val group = if (ChronoUnit.DAYS.between(voicemail.dateTime, ZonedDateTime.now()) < 1)
                    "Today"
                else if (ChronoUnit.WEEKS.between(voicemail.dateTime, ZonedDateTime.now()) < 1)
                    "Past week"
                else
                    "Older"

                val list = grouped[group]
                if (list != null) {
                    grouped[group] = list + voicemail
                }
                else {
                    grouped[group] = listOf(voicemail)
                }
            }
        return grouped
    }

    fun setVoicemailAsRead(voicemail: VoicemailUiModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateVoicemail(voicemail.copy(unread = false))
        }
    }

    fun deleteVoicemail(voicemail: VoicemailUiModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteVoicemail(voicemail)
        }
    }

}

