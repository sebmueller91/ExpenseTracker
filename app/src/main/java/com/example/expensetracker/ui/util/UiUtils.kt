package com.example.expensetracker.ui.util

import android.content.Context
import com.example.expensetracker.model.Currency
import com.example.expensetracker.util.getLocale
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.roundToInt

class UiUtils {
    companion object {
        fun formatMoneyAmount(amount: Double, currency: Currency, context: Context): String {
            val locale = getLocale(context)
            val symbols = DecimalFormatSymbols(locale)
            val formatter = DecimalFormat("#,##0.00", symbols)

            val formattedAmount = formatter.format(amount)

            val roundedValue = BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            val isWholeNumber = BigDecimal(roundedValue).compareTo(BigDecimal(roundedValue.toInt())) == 0
            return if (isWholeNumber) {
                // If the amount is a whole number, format without decimal part
                "${amount.toInt()}${currency.symbol}"
            } else {
                // Otherwise, format with two decimal places
                "$formattedAmount${currency.symbol}"
            }
        }

        fun formatPercentage(value: Double): String {
            return "${value.roundToInt()}%"
        }
    }
}