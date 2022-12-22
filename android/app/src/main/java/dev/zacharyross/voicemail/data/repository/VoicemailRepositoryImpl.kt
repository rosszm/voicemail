package dev.zacharyross.voicemail.data.repository


import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.media.MediaMetadataRetriever
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.data.source.local.VoicemailDatabase
import dev.zacharyross.voicemail.data.source.local.entity.VoicemailEntity
import dev.zacharyross.voicemail.worker.DataSourceSyncWorker
import dev.zacharyross.voicemail.domain.repository.VoicemailRepository
import kotlinx.coroutines.flow.Flow
import dev.zacharyross.voicemail.domain.model.Voicemail
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class VoicemailRepositoryImpl @Inject constructor(
    private val context: Context,
    private val localSource: VoicemailDatabase,
    private val remoteSource: FirebaseFirestore,
    private val defaultDispatcher: CoroutineDispatcher
): VoicemailRepository {

    override suspend fun getVoicemailNumber(): String? {
        val pref = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
        return pref.getString(context.getString(R.string.pref_voicemail_number), null)
    }

    override suspend fun updateVoicemailNumber(userId: String, phoneNumber: String) {
        remoteSource.collection("users")
            .document(userId)
            .set(mapOf("voicemail_number" to phoneNumber), SetOptions.merge())
            .await()
    }

    override suspend fun getInbox(userId: String): Flow<List<Voicemail>> {
        return withContext(defaultDispatcher) {
            return@withContext localSource.voicemailDao().getVoicemailAll(userId)
        }
    }

    override suspend fun getVoicemail(userId: String, id: String): Voicemail {
        return withContext(defaultDispatcher) {
            return@withContext localSource.voicemailDao().getVoicemail(userId, id)
        }
    }

    override suspend fun setVoicemailAsRead(voicemail: Voicemail) {
        withContext(defaultDispatcher) {
            localSource.voicemailDao().updateVoicemail(
                VoicemailEntity(voicemail).copy(unread = false))
        }
    }

    override suspend fun deleteVoicemail(userId: String, id: String) {
        deleteVoicemails(userId, listOf(id))
    }

    override suspend fun deleteVoicemails(userId: String, ids: List<String>) {
        withContext(defaultDispatcher) {
            if (ids.isNotEmpty()) {
                // soft local delete; this immediately removes voicemail messages from the UI
                ids.forEach { localSource.voicemailDao().deleteVoicemailSoft(it) }

                // perform a batch delete of remote copies of the messages
                val batch = Firebase.firestore.batch()
                remoteSource.collection("users")
                    .document(userId)
                    .collection("messages")
                    .whereIn(FieldPath.documentId(), ids).get().await()
                    .documents.forEach { batch.delete(it.reference) }
                var sucessful = false
                batch.commit().addOnSuccessListener { sucessful = true }.await()
                delay(5) // wait for the successful variable to be set

                // hard local delete; only performed if the remote delete is successful
                if (sucessful) {
                    ids.forEach { localSource.voicemailDao().deleteVoicemailHard(it) }
                }
            }
        }
    }

    override suspend fun pullVoicemail(userId: String, id: String) {
        withContext(defaultDispatcher) {
            val document = remoteSource.collection("users")
                .document(userId)
                .collection("messages")
                .document(id)
                .get().await()
            val voicemail = firebaseDataToVoicemailEntity(userId, document)
            if (voicemail != null)
                localSource.voicemailDao().insertVoicemail(voicemail)
        }
    }

    override suspend fun syncData(userId: String) {
        syncPreferences(userId)
        syncMessages(userId)
    }

    private suspend fun syncMessages(userId: String) {
        withContext(defaultDispatcher) {
            val deleted = localSource.voicemailDao().getSoftDeletedIds(userId)
            deleteVoicemails(userId, deleted)
            val messages = remoteSource
                .collection("users")
                .document(userId)
                .collection("messages").get().await()
                .documents.mapNotNull {
                    firebaseDataToVoicemailEntity(userId, it)
                }
            localSource.voicemailDao().insertVoicemails(*messages.toTypedArray())
        }
    }

    private suspend fun syncPreferences(userId: String) {
        remoteSource.collection("users")
            .document(userId)
            .get().addOnSuccessListener { user ->
                val voicemailNumber = user.getString("voicemail_number")
                context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
                    .edit()
                    .putString(context.getString(R.string.pref_voicemail_number), voicemailNumber ?: "")
                    .apply()
            }
            .await()
    }

    private fun firebaseDataToVoicemailEntity(userId: String, document: DocumentSnapshot): VoicemailEntity? {
        val message = document.data
        return if (message != null)
            VoicemailEntity(
                id = document.id,
                userId = userId,
                fromNumber = message["from_number"] as String,
                audioUrl = message["audio_url"] as String,
                audioDuration = getAudioDuration(message["audio_url"] as String) ?: 0,
                transcription = message["transcription"] as String,
                unread = true,
                dateTime = (message["timestamp"] as Timestamp)
                    .toDate().toInstant().atZone(ZoneId.of("UTC")),
            )
        else null
    }

    private fun getAudioDuration(uri: String): Long? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
    }
}