package com.example.expensetracker.ui.util

import com.example.expensetracker.model.Currency

class UiUtils {
    companion object {
        fun formatMoneyAmount(amount: Double, currency: Currency): String {
            val roundedAmount = String.format("%.2f", amount).toDouble()
            return if (roundedAmount % 1.0 == 0.0) {
                "${roundedAmount.toInt()}$currency"
            } else {
                "${String.format("%.2f", amount)}${currency.symbol}"
            }
        }
    }
}