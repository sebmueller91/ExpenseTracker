package com.example.data.database.objects

import com.example.data.model.Participant
import io.realm.kotlin.types.RealmObject
import java.util.UUID

internal class ParticipantObject : RealmObject {
    var name: String = ""
    var id: UUID = UUID.randomUUID()
}

internal fun ParticipantObject.toParticipant() : Participant {
    throw NotImplementedError()
}

internal fun Participant.toParticipantObject() : ParticipantObject {
    throw NotImplementedError()
}