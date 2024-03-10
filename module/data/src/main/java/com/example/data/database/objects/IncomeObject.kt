package com.example.data.database.objects

import com.example.data.model.Transaction
import io.realm.kotlin.types.RealmObject
import java.util.Date

internal class IncomeObject : RealmObject {
    var amount: Double = 0.0
    var date = Date()
    var purpose: String = ""
    var receivedBy: ParticipantObject = ParticipantObject()
    var splitBetween: List<ParticipantObject> = listOf()

}

internal fun IncomeObject.toIncome(): Transaction.Income {
    return Transaction.Income(
        amount = amount,
        date = date,
        purpose = purpose,
        receivedBy = receivedBy.toParticipant(),
        splitBetween = splitBetween.map { it.toParticipant() }
    )
}

internal fun Transaction.Income.toIncomeObject(): IncomeObject {
    throw NotImplementedError()
}