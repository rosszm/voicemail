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
     * Returns the current voicemail number.
     */
    suspend fun getVoicemailNumber(): String?

    /**
     * Updates the voicemail number of a given user.
     */
    suspend fun updateVoicemailNumber(userId: String, phoneNumber: String)

    /**
     * Gets all the voicemail messages that have been sent to a given phone number.
     */
    suspend fun getInbox(userId: String): Flow<List<Voicemail>>

    /**
     * Gets the voicemail associated with a specific id.
     */
    suspend fun getVoicemail(userId: String, id: String): Voicemail

    /**
     * Sets a voicemail to be read.
     */
    suspend fun setVoicemailAsRead(voicemail: Voicemail)

    /**
     * Removes a voicemail message from repository.
     */
    suspend fun deleteVoicemail(userId: String, id: String)

    /**
     * Removes a collection of voicemail messages from repository.
     */
    suspend fun deleteVoicemails(userId: String, ids: List<String>)

    /**
     * Pulls a voicemail from the remote data store.
     */
    suspend fun pullVoicemail(userId: String, id: String)

    /**
     * Syncs the local and remote data of a user.
     */
    suspend fun syncData(userId: String)
}