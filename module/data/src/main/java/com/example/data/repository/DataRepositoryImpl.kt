package com.example.data.repository

import com.example.core.model.Group
import com.example.core.model.Transaction
import com.example.data.database.objects.GroupObject
import com.example.data.database.objects.toGroup
import com.example.data.database.objects.toGroupObject
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class DataRepositoryImpl(
    val realmDb: Realm
): DataRepository{
    override val groups: Flow<List<Group>> = realmDb
            .query(GroupObject::class)
            .asFlow()
        .map {results -> results.list.map { it.toGroup() }}


    override suspend fun addGroup(group: Group) {
        realmDb.write {
            copyToRealm(group.toGroupObject(), UpdatePolicy.ALL)
        }
    }

    override fun addTransaction(groupId: UUID, transaction: Transaction) {
        TODO("Not yet implemented")
    }
}