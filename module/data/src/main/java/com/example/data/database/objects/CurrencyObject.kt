package com.example.data.database.objects

import com.example.core.model.Currency
import io.realm.kotlin.types.RealmObject

internal class CurrencyObject : RealmObject {
    var abbreviation: String = ""
}

internal fun CurrencyObject.toCurrency(): Currency =
    Currency.values().first { it.abbreviation == abbreviation }


internal fun Currency.toCurrencyObject(): CurrencyObject = CurrencyObject().apply {
    abbreviation = this@toCurrencyObject.abbreviation
}