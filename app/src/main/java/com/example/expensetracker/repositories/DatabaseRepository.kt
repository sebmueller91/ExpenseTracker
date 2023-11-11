package com.example.expensetracker.repositories

import com.example.expensetracker.model.Group
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface DatabaseRepository {
    val groups: StateFlow<List<Group>>
    fun addEvent(group: Group)
    fun deleteEvent(groupId: UUID)
}