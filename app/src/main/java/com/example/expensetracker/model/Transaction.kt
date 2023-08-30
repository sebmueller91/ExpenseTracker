package com.example.expensetracker.model

import java.util.Date

sealed class Transaction(
    val purpose: String,
    val splitBetween: List<Participant>,
    val moneyAmout: MoneyAmout,
    val date: Date,
    val currency: Currency
) {
    class Expense(
        val paidBy: Participant,
        purpose: String,
        splitBetween: List<Participant>,
        moneyAmout: MoneyAmout,
        date: Date,
        currency: Currency
    ) : Transaction(purpose, splitBetween, moneyAmout, date, currency)

    class Payment(
        val fromParticipant: Participant,
        val toParticipant: Participant,
        purpose: String,
        splitBetween: List<Participant>,
        moneyAmout: MoneyAmout,
        date: Date,
        currency: Currency
    ) : Transaction(purpose, splitBetween, moneyAmout, date, currency)
    class Income(
        val receivedBy: Participant,
        purpose: String,
        splitBetween: List<Participant>,
        moneyAmout: MoneyAmout,
        date: Date,
        currency: Currency
    ) : Transaction(purpose, splitBetween, moneyAmout, date, currency)
}
