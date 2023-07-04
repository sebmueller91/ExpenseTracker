package com.example.expensetracker.model

data class Expense(
    val paidBy: Participant,
    val paidFor: List<Participant>,
    val amount: Double,
)