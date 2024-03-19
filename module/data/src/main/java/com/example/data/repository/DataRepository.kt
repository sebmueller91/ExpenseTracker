package com.example.data.repository

import com.example.core.model.Group
import com.example.core.model.SettleUpGroup
import com.example.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DataRepository {
    val groups: Flow<List<SettleUpGroup>>
    suspend fun addGroup(group: Group)
    suspend fun addTransactionToGroup(groupId: UUID, transaction: Transaction)
}