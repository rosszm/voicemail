package dev.zacharyross.voicemail.data.source.remote

import dev.zacharyross.voicemail.domain.model.Voicemail
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path


//TODO: implement Voicemail API using retrofit
interface VoicemailApi {
    /** Gets a list of all the voicemails sent to a given recipient. */
    @GET("/inbox/{phoneNumber}")
    fun getInbox(@Path("phoneNumber") phoneNumber: String): List<Voicemail> {
        TODO("Not yet implemented")
    }

    /** Deletes all the voicemails sent to a given recipient. */
    @DELETE("/inbox/{phoneNumber}")
    fun clearInbox(@Path("phoneNumber") phoneNumber: String) {
        TODO("Not yet implemented")
    }

    /** Gets an individual voicemail message. */
    @GET("/voicemail/{id}")
    fun getVoicemail(@Path("id") id: Int): Voicemail {
        TODO("Not yet implemented")
    }

    /** Toggles whether or not a voicemail message has been read. */
    @PUT("/voicemail/{id}/toggle_read")
    fun toggleReadVoicemail(id: Int) {
        TODO("Not yet implemented")
    }

    /** Deletes an individual voicemail message. */
    @DELETE("/voicemail/{id}")
    fun deleteVoicemail(id: Int) {
        TODO("Not yet implemented")
    }
}