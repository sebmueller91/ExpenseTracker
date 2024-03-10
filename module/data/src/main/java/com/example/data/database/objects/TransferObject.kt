package com.example.data.database.objects

import com.example.core.model.Transaction
import io.realm.kotlin.types.RealmObject
import java.util.Date

internal class TransferObject : RealmObject{
    var fromParticipant: ParticipantObject = ParticipantObject()
    var toParticipant: ParticipantObject = ParticipantObject()
    var amount: Double = 0.0
    var timestamp: Long = 0
    var purpose: String = ""
}

internal fun TransferObject.toTransfer(): Transaction.Transfer = Transaction.Transfer(
    fromParticipant = this@toTransfer.fromParticipant.toParticipant(),
    toParticipant = this@toTransfer.toParticipant.toParticipant(),
    amount = this@toTransfer.amount,
    date = Date(this@toTransfer.timestamp),
    purpose = this@toTransfer.purpose
)

internal fun Transaction.Transfer.toTransferObject(): TransferObject = TransferObject().apply {
    fromParticipant = this@toTransferObject.fromParticipant.toParticipantObject()
    toParticipant = this@toTransferObject.toParticipant.toParticipantObject()
    amount = this@toTransferObject.amount
    timestamp = this@toTransferObject.date.time
    purpose = this@toTransferObject.purpose
}