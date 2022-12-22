package dev.zacharyross.voicemail.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import dev.zacharyross.voicemail.domain.repository.ClientRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class ClientRepositoryImpl @Inject constructor(
    private val storageSource: FirebaseFirestore,
    private val messagingSource: FirebaseMessaging,
): ClientRepository {

    override suspend fun refreshToken() {
        clientReference()
            .set(mapOf("last_updated" to Timestamp.now()), SetOptions.merge())
            .await()
    }

    override suspend fun setClientUser(userId: String) {
        clientReference()
            .set(mapOf(
                "user" to userId,
                "last_updated" to Timestamp.now()
            ))
            .await()
    }

    override suspend fun removeClientUser() {
        clientReference()
            .set(mapOf("last_updated" to Timestamp.now()))
            .await()
    }

    private suspend fun clientReference(): DocumentReference {
        val token = messagingSource.token.await()
         return storageSource
            .collection("clients")
            .document(token)
    }
}