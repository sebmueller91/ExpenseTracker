package com.example.expensetracker.model

import java.util.UUID

data class Event(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val participants: List<Participant>
)