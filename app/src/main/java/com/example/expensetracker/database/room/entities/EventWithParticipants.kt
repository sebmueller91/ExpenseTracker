package com.example.expensetracker.database.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity()
data class EventWithParticipants(
    @Embedded val event: EventEntity,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "eventId"
    )
    val participants: List<ParticipantEntity>
)