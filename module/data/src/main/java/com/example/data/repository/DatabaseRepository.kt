package com.example.data.repository

import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface DatabaseRepository {
    val groups: StateFlow<List<com.example.data.model.Group>>
    fun addGroup(group: com.example.data.model.Group)
    fun addTransaction(groupId: UUID, transaction: com.example.data.model.Transaction)
    suspend fun getGroup(groupId: UUID): com.example.data.model.Group?
}