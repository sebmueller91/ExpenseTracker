package com.example.data.database.objects

import com.example.core.model.Participant
import io.realm.kotlin.types.RealmObject
import java.util.UUID

internal class ParticipantObject : RealmObject {
    var name: String = ""
    var id: UUID = UUID.randomUUID()
}

internal fun ParticipantObject.toParticipant() : Participant = Participant(
    name = this@toParticipant.name,
    id = this@toParticipant.id
)

internal fun Participant.toParticipantObject() : ParticipantObject = ParticipantObject().apply {
    name = this@toParticipantObject.name
    id = this@toParticipantObject.id
}