package com.example.expensetracker.ui.util

import android.content.Context
import com.example.expensetracker.R
import com.example.data.model.Currency
import com.example.data.model.Participant
import com.example.expensetracker.util.getLocale
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Date
import kotlin.math.roundToInt

class UiUtils {
    companion object {
        fun formatMoneyAmount(amount: Double, currency: com.example.data.model.Currency, context: Context): String {
            val locale = getLocale(context)
            val symbols = DecimalFormatSymbols(locale)
            val formatter = DecimalFormat("#,##0.00", symbols)

            val formattedAmount = formatter.format(amount)

            val roundedValue = BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            val isWholeNumber =
                BigDecimal(roundedValue).compareTo(BigDecimal(roundedValue.toInt())) == 0
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

        fun formatParticipantsList(participants: List<com.example.data.model.Participant>, context: Context): String {
            if (participants.isEmpty()) {
                return ""
            } else if (participants.size == 1) {
                return participants.first().name
            } else {
                return participants
                    .joinToString(", ") { it.name }
                    .replaceLastOccurrenceOf(", ", context.getString(R.string.and))
            }
        }

        fun formatDate(date: Date, context: Context): String {
            val locale = getLocale(context)
            val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
            return formatter.format(date)
        }

        private fun String.replaceLastOccurrenceOf(toReplace: String, replacement: String): String {
            val index = lastIndexOf(toReplace)
            if (index < 0) {
                Timber.e("Could not replace substring <$toReplace> by <$replacement> in string <$this>")
                return this
            }
            return substring(0, index) + replacement + substring(index + toReplace.length)
        }
    }
}