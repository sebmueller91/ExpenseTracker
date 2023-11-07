package com.example.expensetracker.database.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.expensetracker.model.Participant
import java.util.UUID

@Entity(
    tableName = "participants",
    foreignKeys = [ForeignKey(
        entity = EventEntity::class,
        parentColumns = ["eventId"],
        childColumns = ["eventId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ParticipantEntity(
    @PrimaryKey val participantId: String,
    val name: String,
    @ColumnInfo(index = true) val eventId: Long
)

fun ParticipantEntity.toParticipant(): Participant {
    return Participant(
        name = name,
        id = UUID.fromString(participantId)
    )
}

fun Participant.toParticipantEntity(eventId: Long): ParticipantEntity {
    return ParticipantEntity(
        name = name,
        participantId = id.toString(),
        eventId = eventId
    )
}