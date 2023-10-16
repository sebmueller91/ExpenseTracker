package com.example.expensetracker.model

val CURRENCIES = listOf(Currency(name = "Euro", abbreviation = "EUR", symbol = '€'))

data class Currency(
    val name: String,
    val abbreviation: String,
    val symbol: Char
)