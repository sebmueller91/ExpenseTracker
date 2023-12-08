package com.example.expensetracker.model

enum class Currency(
    val currency_name: String,
    val abbreviation: String, // TODO: Is this needed?
    val symbol: Char
) {
    EURO("Euro", "EUR", '€'),
    USD("United States Dollar", "USD", '$'),
    POUND_STERLING("Pound Sterling", "GBP", '£'),
    JPY("Japanese Yen", "JPY", '¥'),
    AUD("Australian Dollar", "AUD", 'A'),
    CAD("Canadian Dollar", "CAD", 'C'),
    CHF("Swiss Franc", "CHF", '₣'),
    CNY("Chinese Yuan Renminbi", "CNY", '¥'),
    SEK("Swedish Krona", "SEK", 'k'),
    NZD("New Zealand Dollar", "NZD", 'N'),
    MXN("Mexican Peso", "MXN", 'M'),
    SGD("Singapore Dollar", "SGD", 'S'),
    HKD("Hong Kong Dollar", "HKD", 'H'),
    NOK("Norwegian Krone", "NOK", 'N'),
    KRW("South Korean Won", "KRW", '₩'),
    TRY("Turkish Lira", "TRY", '₺'),
    INR("Indian Rupee", "INR", '₹'),
    RUB("Russian Ruble", "RUB", '₽'),
    BRL("Brazilian Real", "BRL", 'R'),
    VND("Vietnamese Dong", "VND", '₫')
}
