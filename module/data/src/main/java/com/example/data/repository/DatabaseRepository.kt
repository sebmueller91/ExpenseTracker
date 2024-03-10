package com.example.data.repository

import com.example.core.model.Group
import com.example.core.model.Transaction
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface DatabaseRepository {
    val groups: StateFlow<List<Group>>
    fun addGroup(group: Group)
    fun addTransaction(groupId: UUID, transaction: Transaction)
    suspend fun getGroup(groupId: UUID): Group?
}