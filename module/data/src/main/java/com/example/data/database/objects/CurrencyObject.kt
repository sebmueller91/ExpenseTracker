package com.example.data.database.objects

import com.example.data.model.Currency
import io.realm.kotlin.types.RealmObject

internal class CurrencyObject : RealmObject {
}

internal fun CurrencyObject.toCurrency(): Currency {
    throw NotImplementedError()
}

internal fun Currency.toCurrencyObject(): CurrencyObject {
    throw NotImplementedError()
}