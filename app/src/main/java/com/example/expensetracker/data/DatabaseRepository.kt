package com.example.expensetracker.data

import com.example.expensetracker.model.Group
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface DatabaseRepository {
    val groups: StateFlow<List<Group>>
    fun addGroup(group: Group)
    suspend fun getGroup(groupId: UUID): Group?
}