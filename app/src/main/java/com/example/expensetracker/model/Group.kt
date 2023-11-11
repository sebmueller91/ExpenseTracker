package com.example.expensetracker.model

import java.util.UUID

data class Group(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val participants: List<Participant>,
    val currency: Currency,
    val transactions: List<Transaction>
)