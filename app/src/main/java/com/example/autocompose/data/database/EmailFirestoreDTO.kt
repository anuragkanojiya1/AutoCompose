package com.example.autocompose.data.database

data class EmailFirestoreDto(
    val subject: String = "",
    val emailBody: String = "",
    val frequency: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)
