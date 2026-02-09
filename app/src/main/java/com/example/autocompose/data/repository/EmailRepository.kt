package com.example.autocompose.data.repository

import com.example.autocompose.data.database.Dao
import com.example.autocompose.data.database.EmailFirestoreDto
import com.example.autocompose.data.database.EmailRoomDto
import com.example.autocompose.data.database.Entity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class EmailRepository(
    private val dao: Dao
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun uid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private fun userCollection() =
        firestore.collection("users")
            .document(uid())
            .collection("frequent_emails")

    private fun generateDocId(subject: String, body: String): String =
        (subject + body).hashCode().toString()


    suspend fun getMostFrequentEmails(limit: Int): List<Entity> =
        dao.getMostFrequentEmails(limit)

    suspend fun findEmail(subject: String, body: String): Entity? =
        dao.findEmail(subject, body)

    suspend fun saveOrUpdateEmail(subject: String, body: String) {
        val existing = dao.findEmail(subject, body)
        val docRef = userCollection().document(generateDocId(subject, body))

        if (existing != null) {
            dao.incrementFrequency(existing.id)

            docRef.set(
                mapOf(
                    "frequency" to FieldValue.increment(1),
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
        } else {
            dao.insertEmails(
                Entity(
                    subject = subject, emailBody = body, frequency = 1,
//                    updatedAt = FieldValue.serverTimestamp()
                )
            )

            docRef.set(
                EmailFirestoreDto(
                    subject = subject,
                    emailBody = body,
                    frequency = 1,
                    updatedAt = FieldValue.serverTimestamp()
                )
            )
        }
    }

    suspend fun deleteById(entity: Entity) {
        dao.deleteEmail(entity.id)
        userCollection()
            .document(generateDocId(entity.subject, entity.emailBody))
            .delete()
    }

    suspend fun deleteByContent(subject: String, body: String): Boolean {
        val entity = dao.findEmail(subject, body) ?: return false
        deleteById(entity)
        return true
    }

    suspend fun clearAll() {
        dao.deleteAllEmails()

        val snapshot = userCollection().get().await()
        val batch = firestore.batch()
        snapshot.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    suspend fun syncFromFirestoreToRoom() {
        val snapshot = userCollection().get().await()
        snapshot.documents.forEach {
            val dto = it.toObject(EmailRoomDto::class.java) ?: return@forEach
            if (dao.findEmail(dto.subject, dto.emailBody) == null) {
                dao.insertEmails(
                    Entity(
                        subject = dto.subject,
                        emailBody = dto.emailBody,
                        frequency = dto.frequency,
                    )
                )
            }
        }
    }
}
