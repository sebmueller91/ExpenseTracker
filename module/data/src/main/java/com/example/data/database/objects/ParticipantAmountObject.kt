package com.example.data.database.objects

import com.example.core.model.ParticipantAmount
import io.realm.kotlin.types.RealmObject

internal class ParticipantAmountObject : RealmObject {
    var participant: ParticipantObject? = null
    var amount: Double = 0.0
}

internal fun ParticipantAmountObject.toParicipantAmount() = ParticipantAmount(
    participant = this@toParicipantAmount.participant!!.toParticipant(),
    amount = this@toParicipantAmount.amount
)

internal fun ParticipantAmount.toParicipantAmountObject() = ParticipantAmountObject().apply {
    participant = this@toParicipantAmountObject.participant.toParticipantObject()
    amount = this@toParicipantAmountObject.amount
}