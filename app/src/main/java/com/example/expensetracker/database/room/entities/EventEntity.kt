package com.example.expensetracker.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.model.Event
import com.example.expensetracker.model.Participant
import java.util.UUID

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val eventId: String,
    val name: String
)

fun EventEntity.toEvent(participants: List<Participant>): Event {
    return Event(
        id = UUID.fromString(this.eventId),
        name = this.name,
        participants = participants
    )
}

fun Event.toEventEntity() : EventEntity{
    return EventEntity(
        eventId = this.id.toString(),
        name = this.name
    )
}