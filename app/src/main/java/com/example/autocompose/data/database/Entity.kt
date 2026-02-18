package com.example.autocompose.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.gson.annotations.SerializedName

@Entity(tableName = "emails")
data class Entity(
    @SerializedName("subject")
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("subject")
    val subject : String,
    @SerializedName("emailBody")
    val emailBody: String,
    @SerializedName("frequency")
    val frequency: Int = 1,
)
