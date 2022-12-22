package dev.zacharyross.voicemail.domain.repository


interface ClientRepository {
    /**
     * Updates a client token. If the token does not already exist in the repository, it will be
     * added.
     */
    suspend fun refreshToken()

    /**
     * Sets a given user as the user of this client.
     */
    suspend fun setClientUser(userId: String)

    /**
     * Removes the user of this client.
     */
    suspend fun removeClientUser()
}