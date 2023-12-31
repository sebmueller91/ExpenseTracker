package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Transaction

interface EventCostCalculator {
    fun execute(transactions: List<Transaction>): Double
}

class EventCostCalculatorImpl : EventCostCalculator {
    override fun execute(transactions: List<Transaction>): Double {
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