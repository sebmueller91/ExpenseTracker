package com.example.data.database.objects

import com.example.core.model.Transaction
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import java.util.Date

internal class IncomeObject : RealmObject {
    var amount: Double = 0.0
    var timestamp: Long = 0L
    var purpose: String = ""
    var receivedBy: ParticipantObject? = ParticipantObject()
    var splitBetween: RealmList<ParticipantObject> = realmListOf()

}

internal fun IncomeObject.toIncome(): Transaction.Income = Transaction.Income(
    amount = amount,
    date = Date(timestamp),
    purpose = purpose,
    receivedBy = receivedBy?.toParticipant()!!, // TODO: Get rid of !!
    splitBetween = splitBetween.map { it.toParticipant() }
)

internal fun Transaction.Income.toIncomeObject(): IncomeObject = IncomeObject().apply {
    amount = this@toIncomeObject.amount
    timestamp = this@toIncomeObject.date.time
    purpose = this@toIncomeObject.purpose
    receivedBy = this@toIncomeObject.receivedBy.toParticipantObject()
    splitBetween = this@toIncomeObject.splitBetween.map { it.toParticipantObject() }.toRealmList()
}