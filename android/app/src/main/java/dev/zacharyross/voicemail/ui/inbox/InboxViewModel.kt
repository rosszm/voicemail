package dev.zacharyross.voicemail.ui.inbox


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import dev.zacharyross.voicemail.ui.model.DisplayContact
import dev.zacharyross.voicemail.ui.model.VoicemailUiModel
import dev.zacharyross.voicemail.ui.util.getContactInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
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
    private var inboxFlow: Flow<List<VoicemailUiModel>> = flowOf()

    init {
        collectInboxFlow()
    }

    private fun collectInboxFlow() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getInbox().flowWithLifecycle()




                .collectLatest {
                val inbox = it.map { voicemail ->
                    val contact = getContactInfo(appContext.contentResolver, voicemail.fromNumber)
                    VoicemailUiModel(voicemail, contact)
                }
                inboxFlow = flowOf(inbox)
            }
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

    fun inboxIsEmpty(): Boolean {
        return groupedInbox.isEmpty()
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

    fun updateInboxWithContacts() {
        groupedInbox.forEach { entry ->
            entry.value.map { voicemail ->
                voicemail.copy(
                    contact = getContactInfo(appContext.contentResolver, voicemail.fromNumber))
            }
        }
    }


}

