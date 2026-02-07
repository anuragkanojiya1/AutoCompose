package com.example.autocompose.data.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class EmailFirestoreDto(
    val subject: String = "",
    val emailBody: String = "",
    val frequency: Int = 1,
    val updatedAt: FieldValue = FieldValue.serverTimestamp()
)

data class EmailRoomDto(
    val subject: String = "",
    val emailBody: String = "",
    val frequency: Int = 1,
)
