package com.example.expensetracker.model

import io.realm.kotlin.types.RealmObject
import java.util.Date

sealed class Transaction(
    val purpose: String,
    val amount: Double,
    val date: Date
) : RealmObject {
    class Expense(
        val paidBy: Participant,
        val splitBetween: List<Participant>,
        purpose: String,
        amount: Double,
        date: Date
    ) : Transaction(purpose, amount, date)

    class Transfer(
        val fromParticipant: Participant,
        val toParticipant: Participant,
        purpose: String,
        amount: Double,
        date: Date
    ) : Transaction(purpose, amount, date)
    class Income(
        val receivedBy: Participant,
        val splitBetween: List<Participant>,
        purpose: String,
        amount: Double,
        date: Date
    ) : Transaction(purpose, amount, date)
}
