package com.example.expensetracker.repositories

import com.example.expensetracker.database.room.dao.EventDao
import com.example.expensetracker.database.room.dao.ParticipantDao
import com.example.expensetracker.model.Event
import com.example.expensetracker.model.Participant
import kotlinx.coroutines.flow.Flow
import java.util.UUID

// TODO: Set up DI
class DatabaseRepository(
    private val eventDao: EventDao,
    private val participantDao: ParticipantDao
) {
    fun getAllEvents(): Flow<List<Event>> {
        // TODO
    }

    suspend fun insertEvent(event: Event) {
        // TODO
    }

    suspend fun addParticipantToEvent(eventId: UUID, participant: Participant) {
        // TODO
    }

    fun deleteParticipant(participantId: UUID) {
        // TODO
    }
}