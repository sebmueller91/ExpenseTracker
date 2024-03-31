package com.example.data.repository

import com.example.core.model.Group
import com.example.core.model.SettleUpGroup
import com.example.core.model.Transaction
import com.example.core.services.GroupCostsCalculator
import com.example.core.services.IndividualPaymentAmountCalculator
import com.example.core.services.IndividualPaymentPercentageCalculator
import com.example.core.services.SettleUpCalculator
import com.example.data.database.objects.SettleUpGroupObject
import com.example.data.database.objects.toExpenseObject
import com.example.data.database.objects.toGroup
import com.example.data.database.objects.toGroupObject
import com.example.data.database.objects.toIncomeObject
import com.example.data.database.objects.toParicipantAmountObject
import com.example.data.database.objects.toParicipantPercentageObject
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
    val settleUpCalculator: SettleUpCalculator,
    val groupCostsCalculator: GroupCostsCalculator,
    val individualPaymentAmountCalculator: IndividualPaymentAmountCalculator,
    val individualPaymentPercentageCalculator: IndividualPaymentPercentageCalculator
) : DataRepository {

    override val groups: Flow<List<SettleUpGroup>> = realm
        .query(SettleUpGroupObject::class)
        .asFlow()
        .map { results -> results.list.map { it.toSettleUpGroup() } }

    override suspend fun addGroup(group: Group) {
        realm.write {
            val settleUpGroup = SettleUpGroupObject().apply {
                this.group = group.toGroupObject()
            }.recalculate()
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
                newGroupObject
                    .removeSettleUpTransaction(transaction.id.toString())
                    .recalculateIndividualValues()
            } else {
                newGroupObject.recalculate()
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

    private fun SettleUpGroupObject.removeSettleUpTransaction(transactionId: String): SettleUpGroupObject {
        settleUpTransactions.removeIf { it.id == transactionId }
        return this
    }

    private fun SettleUpGroupObject.recalculate(): SettleUpGroupObject {
        group?.toGroup()?.let { group ->
            settleUpTransactions =
                settleUpCalculator.execute(group).map { it.toTransferObject() }.toRealmList()
            groupCosts = groupCostsCalculator.execute(group.transactions)
            individualPaymentAmount = individualPaymentAmountCalculator.execute(group)
                .map { it.toParicipantAmountObject() }.toRealmList()
            individualPaymentPercentage = individualPaymentPercentageCalculator.execute(group)
                .map { it.toParicipantPercentageObject() }.toRealmList()
        }
        return this
    }

    private fun SettleUpGroupObject.recalculateIndividualValues(): SettleUpGroupObject {
        group?.toGroup()?.let { group ->
            individualPaymentAmount = individualPaymentAmountCalculator.execute(group)
                .map { it.toParicipantAmountObject() }.toRealmList()
            individualPaymentPercentage = individualPaymentPercentageCalculator.execute(group)
                .map { it.toParicipantPercentageObject() }.toRealmList()
        }
        return this
    }
}