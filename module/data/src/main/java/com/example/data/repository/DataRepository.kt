package com.example.data.repository

import com.example.core.model.Group
import com.example.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DataRepository {
    val groups: Flow<List<Group>>
    suspend fun addGroup(group: Group)
    fun addTransaction(groupId: UUID, transaction: Transaction)
}