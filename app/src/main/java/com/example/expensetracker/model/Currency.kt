package com.example.expensetracker.model

enum class Currency(
    val currency_name: String,
    val abbreviation: String,
    val symbol: Char
) {
    EURO( "Euro", "EUR",'€'),
    USD("United States Dollar", "USD", '$'),
    POUND_STERLING("Pound Sterling", "GBP", '£')
}