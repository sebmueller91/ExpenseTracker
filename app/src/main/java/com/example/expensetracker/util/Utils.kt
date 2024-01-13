package com.example.expensetracker.util

import android.content.Context
import androidx.core.os.ConfigurationCompat
import java.util.Locale
import kotlin.math.abs

fun getLocale(context: Context): Locale {
    val configuration = context.resources.configuration
    return ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
}

fun Double.isEqualTo(d: Double, threshold: Double = 0.005): Boolean {
    return abs(this- d) < threshold
}
fun Double.isSmallerThan(d: Double, threshold: Double = 0.005): Boolean {
    return !this.isEqualTo(d, threshold) && this < d
}

fun Double.isBiggerThan(d: Double, threshold: Double = 0.005): Boolean {
    val a = !this.isEqualTo(d, threshold)
    val b = this > d
    return a && b
}