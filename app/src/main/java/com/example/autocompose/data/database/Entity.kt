package com.example.autocompose.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class Entity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject : String,
    val emailBody: String,
    val frequency: Int = 1
)
