package com.example.expensetracker.util

import android.content.Context
import androidx.core.os.ConfigurationCompat
import java.util.Locale

fun getLocale(context: Context): Locale {
    val configuration = context.resources.configuration
    return ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
}