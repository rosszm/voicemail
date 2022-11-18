package dev.zacharyross.voicemail.data.source.local.dao

import androidx.room.*
import dev.zacharyross.voicemail.data.source.local.entity.VoicemailEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface VoicemailDao {
    @Transaction
    @Query("SELECT * FROM voicemail")
    fun getVoicemails(): Flow<List<VoicemailEntity>>

    @Transaction
    @Query("SELECT * FROM voicemail WHERE id = :id LIMIT 1")
    fun getVoicemailFromId(id: Int): VoicemailEntity

    @Transaction
    @Query("SELECT * FROM voicemail WHERE from_number LIKE :phoneNumber LIMIT 1")
    fun getVoicemailsFrom(phoneNumber: String): VoicemailEntity

    @Update
    suspend fun updateVoicemail(voicemail: VoicemailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoicemail(voicemail: VoicemailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoicemails(vararg voicemail: VoicemailEntity)

    @Delete
    suspend fun deleteVoicemail(voicemail: VoicemailEntity)

    @Query("DELETE FROM voicemail")
    suspend fun deleteAllVoicemails()
}
