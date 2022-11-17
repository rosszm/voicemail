package dev.zacharyross.voicemail.domain.repository

import dev.zacharyross.voicemail.domain.model.Voicemail
import kotlinx.coroutines.flow.Flow


/**
 * The voicemail repository.
 *
 * Handles sending and retrieving voicemail data, as well as synchronization between data sources.
 */
interface VoicemailRepository {
    /**
     * Gets all the voicemail messages that have been sent to a given phone number.
     */
    fun getInbox(): Flow<List<Voicemail>>

    /**
     * Clears all the voicemail messages from the inbox.
     */
    fun clearInbox()

    /**
     * Gets the voicemail associated with a specific id.
     */
    fun getVoicemail(id: Int): Voicemail

    /**
     * Updates the voicemail in the repository.
     */
    fun updateVoicemail(voicemail: Voicemail)

    /**
     * Removes a voicemail message from repository.
     */
    fun deleteVoicemail(voicemail: Voicemail)

}