package com.example.core.model

import java.util.Date
import java.util.UUID

sealed class Transaction(
    val purpose: String,
    val amount: Double,
    val date: Date,
    val id: UUID
) {
    class Expense(
        val paidBy: Participant,
        val splitBetween: List<Participant>,
        purpose: String,
        amount: Double,
        date: Date,
        id: UUID = UUID.randomUUID()
    ) : Transaction(purpose, amount, date, id)

    class Transfer(
        val fromParticipant: Participant,
        val toParticipant: Participant,
        purpose: String,
        amount: Double,
        date: Date,
        id: UUID = UUID.randomUUID()
    ) : Transaction(purpose, amount, date, id)
    class Income(
        val receivedBy: Participant,
        val splitBetween: List<Participant>,
        purpose: String,
        amount: Double,
        date: Date,
        id: UUID = UUID.randomUUID()
    ) : Transaction(purpose, amount, date, id)
}
