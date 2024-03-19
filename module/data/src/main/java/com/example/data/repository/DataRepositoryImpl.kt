package com.example.data.repository

import com.example.core.model.Group
import com.example.core.model.SettleUpGroup
import com.example.core.model.Transaction
import com.example.core.services.SettleUp
import com.example.data.database.objects.SettleUpGroupObject
import com.example.data.database.objects.toExpenseObject
import com.example.data.database.objects.toGroup
import com.example.data.database.objects.toGroupObject
import com.example.data.database.objects.toIncomeObject
import com.example.data.database.objects.toSettleUpGroup
import com.example.data.database.objects.toTransferObject
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class DataRepositoryImpl(
    val realm: Realm,
    val settleUp: SettleUp
) : DataRepository {

    override val groups: Flow<List<SettleUpGroup>> = realm
        .query(SettleUpGroupObject::class)
        .asFlow()
        .map { results -> results.list.map { it.toSettleUpGroup() } }

    override suspend fun addGroup(group: Group) {
        realm.write {
            val settleUpTransactions = settleUp.execute(group)
            val settleUpGroup = SettleUpGroupObject().apply {
                this.group = group.toGroupObject()
                this.settleUpTransactions =
                    settleUpTransactions.map { it.toTransferObject() }.toRealmList()
            }
            copyToRealm(settleUpGroup, UpdatePolicy.ALL)
        }
    }

    override suspend fun addTransactionToGroup(groupId: UUID, transaction: Transaction) {
        val filterGroups =
            realm.query<SettleUpGroupObject>("group.id == $0", groupId.toString()).find().first()

        realm.write {
            val groupObject = findLatest(filterGroups) ?: return@write
            val isSettleUpTransaction =
                groupObject.settleUpTransactions.any { it.id == transaction.id.toString() }

            val newGroupObject = groupObject.copyFromRealm()
            newGroupObject._id = groupObject._id

            newGroupObject.addTransaction(transaction)

            if (isSettleUpTransaction) {
                newGroupObject.removeSettleUpTransaction(transaction.id.toString())
            } else {
                newGroupObject.recalculateSettleUpTransactions()
            }
            copyToRealm(newGroupObject, UpdatePolicy.ALL)
        }
    }

    private fun SettleUpGroupObject.addTransaction(transaction: Transaction) = when (transaction) {
        is Transaction.Expense -> group?.expenses?.add(
            transaction.toExpenseObject()
        )

        is Transaction.Income -> group?.incomes?.add(
            transaction.toIncomeObject()
        )

        is Transaction.Transfer -> group?.transfers?.add(
            transaction.toTransferObject()
        )
    }

    private fun SettleUpGroupObject.removeSettleUpTransaction(transactionId: String) {
        settleUpTransactions.removeIf { it.id == transactionId }
    }

    private fun SettleUpGroupObject.recalculateSettleUpTransactions() {
        group?.toGroup()?.let { group ->
            settleUpTransactions =
                settleUp.execute(group).map { it.toTransferObject() }.toRealmList()
        }
    }
}