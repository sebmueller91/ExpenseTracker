package com.example.expensetracker.model

data class Event(
    val name: String,
    val participants: List<Participant>,
    val currency: String
)