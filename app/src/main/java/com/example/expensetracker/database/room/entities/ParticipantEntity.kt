package com.example.expensetracker.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "participant_entity")
data class ParticipantEntity(
    @PrimaryKey val id: String,
    val name: String
)