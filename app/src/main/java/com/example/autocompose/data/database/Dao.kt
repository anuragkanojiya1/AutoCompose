package com.example.autocompose.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface Dao {

    @Insert
    suspend fun insertEmails(emails: Entity)

    @Query("SELECT * FROM emails ORDER BY id DESC")
    suspend fun getEmails(): List<Entity>

    @Query("DELETE FROM emails")
    suspend fun deleteAllEmails()
    
    @Query("SELECT * FROM emails WHERE subject = :subject AND emailBody = :emailBody LIMIT 1")
    suspend fun findEmail(subject: String, emailBody: String): Entity?
    
    @Query("UPDATE emails SET frequency = frequency + 1 WHERE id = :id")
    suspend fun incrementFrequency(id: Int)
    
    @Query("SELECT * FROM emails ORDER BY frequency DESC LIMIT :limit")
    suspend fun getMostFrequentEmails(limit: Int): List<Entity>
}