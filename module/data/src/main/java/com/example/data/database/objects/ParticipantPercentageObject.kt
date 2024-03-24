package com.example.data.database.objects

import com.example.core.model.ParticipantPercentage
import io.realm.kotlin.types.RealmObject

internal class ParticipantPercentageObject : RealmObject {
    var participant: ParticipantObject? = null
    var percentage: Double = 0.0
}

internal fun ParticipantPercentageObject.toParicipantPercentage() = ParticipantPercentage(
    participant = this@toParicipantPercentage.participant!!.toParticipant(),
    percentage = this@toParicipantPercentage.percentage
)

internal fun ParticipantPercentage.toParicipantPercentageObject() = ParticipantPercentageObject().apply {
    participant = this@toParicipantPercentageObject.participant.toParticipantObject()
    percentage = this@toParicipantPercentageObject.percentage
}