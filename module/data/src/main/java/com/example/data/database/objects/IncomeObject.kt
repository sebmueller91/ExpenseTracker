package com.example.data.database.objects

import com.example.core.model.Transaction
import io.realm.kotlin.types.RealmObject
import java.util.Date

internal class IncomeObject : RealmObject {
    var amount: Double = 0.0
    var timestamp: Long = 0L
    var purpose: String = ""
    var receivedBy: ParticipantObject = ParticipantObject()
    var splitBetween: List<ParticipantObject> = listOf()

}

internal fun IncomeObject.toIncome(): Transaction.Income = Transaction.Income(
    amount = amount,
    date = Date(timestamp),
    purpose = purpose,
    receivedBy = receivedBy.toParticipant(),
    splitBetween = splitBetween.map { it.toParticipant() }
)

internal fun Transaction.Income.toIncomeObject(): IncomeObject = IncomeObject().apply {
    amount = this@toIncomeObject.amount
    timestamp = this@toIncomeObject.date.time
    purpose = this@toIncomeObject.purpose
    receivedBy = this@toIncomeObject.receivedBy.toParticipantObject()
    splitBetween = this@toIncomeObject.splitBetween.map { it.toParticipantObject() }
}