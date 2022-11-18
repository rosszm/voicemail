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
    suspend fun getInbox(): Flow<List<Voicemail>>

    /**
     * Clears all the voicemail messages from the inbox.
     */
    suspend fun clearInbox()

    /**
     * Gets the voicemail associated with a specific id.
     */
    suspend fun getVoicemail(id: Int): Voicemail

    /**
     * Updates the voicemail in the repository.
     */
    suspend fun updateVoicemail(voicemail: Voicemail)

    /**
     * Removes a voicemail message from repository.
     */
    suspend fun deleteVoicemail(voicemail: Voicemail)
}