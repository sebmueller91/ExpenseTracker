package com.example.expensetracker.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expensetracker.database.room.dao.EventDao
import com.example.expensetracker.database.room.dao.ParticipantDao
import com.example.expensetracker.database.room.entities.EventEntity
import com.example.expensetracker.database.room.entities.ParticipantEntity

@Database(entities = [ParticipantEntity::class, EventEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun participantDao(): ParticipantDao
    abstract fun eventDao(): EventDao
}