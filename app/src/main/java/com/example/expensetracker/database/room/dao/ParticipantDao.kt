package com.example.expensetracker.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.expensetracker.database.room.entities.ParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipantDao {
    @Query("SELECT * FROM participants WHERE eventId = :eventId")
    fun getAllParticipantsForEvent(eventId: Long): Flow<List<ParticipantEntity>>

    @Insert
    fun insertParticipant(participant: ParticipantEntity)

    @Query("DELETE FROM participants WHERE participantId = :participantId")
    fun deleteParticipant(participantId: String)
}