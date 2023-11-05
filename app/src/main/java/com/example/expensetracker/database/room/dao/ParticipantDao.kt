package com.example.expensetracker.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.expensetracker.database.room.entities.ParticipantEntity

@Dao
interface ParticipantDao {
    @Query("Select * from participant_entity")
    fun getAllParticipants(): List<ParticipantEntity>

    @Insert
    fun insertParticipant(participant: ParticipantEntity)
}