package com.example.data.model

import java.util.UUID

data class Participant(
    val name: String
) {
    val id: UUID = UUID.randomUUID()
}