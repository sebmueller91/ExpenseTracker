package com.example.expensetracker.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expensetracker.database.room.dao.ParticipantDao
import com.example.expensetracker.database.room.entities.ParticipantEntity

@Database(entities = [ParticipantEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun participantDao(): ParticipantDao
}