package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Transaction

class CalculateEventCost(
    val transactions: List<Transaction>
) {
    fun execute(): Double {
        var sum = 0.0
        transactions.forEach {transaction ->
            when (transaction) {
                is Transaction.Expense -> {
                    sum += transaction.amount
                }
                is Transaction.Income -> {
                    sum -= transaction.amount
                }
                is Transaction.Payment -> {
                    // Does not influence event cost
                }
            }
        }
        return sum
    }
}