package dev.zacharyross.voicemail.data.source.local.dao

import androidx.room.*
import dev.zacharyross.voicemail.data.source.local.entity.VoicemailEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface VoicemailDao {
    @Query("SELECT * FROM voicemail WHERE user_id = :uid AND is_deleted = 0")
    fun getVoicemailAll(uid: String): Flow<List<VoicemailEntity>>

    @Query("SELECT * FROM voicemail WHERE user_id = :uid AND id = :id AND is_deleted != 1 LIMIT 1")
    fun getVoicemail(uid: String, id: String): VoicemailEntity

    @Update
    suspend fun updateVoicemail(voicemail: VoicemailEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVoicemail(voicemail: VoicemailEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVoicemails(vararg voicemail: VoicemailEntity)

    @Query("DELETE FROM voicemail WHERE id = :id")
    suspend fun deleteVoicemailHard(id: String)

    @Query("UPDATE voicemail SET is_deleted = 1 WHERE id = :id")
    suspend fun deleteVoicemailSoft(id: String)

    @Query("SELECT id FROM voicemail WHERE user_id = :uid AND is_deleted = 1")
    fun getSoftDeletedIds(uid: String): List<String>
}
