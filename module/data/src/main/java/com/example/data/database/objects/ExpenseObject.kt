package com.example.data.database.objects

import com.example.core.model.Transaction
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import java.util.Date
import java.util.UUID

internal class ExpenseObject : RealmObject{
    var paidBy: ParticipantObject? = ParticipantObject()
    var amount: Double = 0.0
    var timestamp: Long = 0
    var purpose: String = ""
    var splitBetween: RealmList<ParticipantObject> = realmListOf()
    var id: String = ""
}

internal fun ExpenseObject.toExpense(): Transaction.Expense = Transaction.Expense(
    paidBy = this@toExpense.paidBy?.toParticipant()!!, // TODO: Get rid of !!
    amount = this@toExpense.amount,
    date = Date(this@toExpense.timestamp),
    purpose = this@toExpense.purpose,
    splitBetween = this@toExpense.splitBetween.map { it.toParticipant() },
    id = UUID.fromString(this@toExpense.id)
)

internal fun Transaction.Expense.toExpenseObject(): ExpenseObject = ExpenseObject().apply {
    paidBy = this@toExpenseObject.paidBy.toParticipantObject()
    amount = this@toExpenseObject.amount
    timestamp = this@toExpenseObject.date.time
    purpose = this@toExpenseObject.purpose
    splitBetween = this@toExpenseObject.splitBetween.map { it.toParticipantObject() }.toRealmList()
    id = this@toExpenseObject.id.toString()
}