package com.example.autocompose.data.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.gson.annotations.SerializedName

data class EmailFirestoreDto(
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("emailBody")
    val emailBody: String = "",
    @SerializedName("frequency")
    val frequency: Int = 1,
    @SerializedName("updatedAt")
    val updatedAt: FieldValue = FieldValue.serverTimestamp()
)

data class EmailRoomDto(
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("emailBody")
    val emailBody: String = "",
    @SerializedName("frequency")
    val frequency: Int = 1,
)
