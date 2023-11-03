package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Event
import com.example.expensetracker.model.MoneyAmout
import com.example.expensetracker.model.Transaction

class CalculateEventCost(
    val event: Event
) {
    fun execute(): MoneyAmout {
        var eventCosts = MoneyAmout(amount = 0.0, currency = event.currency)
        for (transaction in event.transactions) {
            when (transaction) {
                is Transaction.Payment -> {}
                is Transaction.Expense -> {
                    eventCosts += transaction.moneyAmout
                }

                is Transaction.Income -> {
                    eventCosts -= transaction.moneyAmout
                }
            }
        }
        return eventCosts
    }
}