package com.example.expensetracker.data.repository

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface DatabaseRepository {
    val groups: StateFlow<List<Group>>
    fun addGroup(group: Group)
    fun addTransaction(groupId: UUID, transaction: Transaction)
    suspend fun getGroup(groupId: UUID): Group?
}