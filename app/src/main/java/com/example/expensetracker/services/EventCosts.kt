package com.example.expensetracker.services

import com.example.data.model.Transaction

interface EventCosts {
    fun execute(transactions: List<com.example.data.model.Transaction>): Double
}

class EventCostsImpl : EventCosts {
    override fun execute(transactions: List<com.example.data.model.Transaction>): Double {
        var sum = 0.0
        transactions.forEach {transaction ->
            when (transaction) {
                is com.example.data.model.Transaction.Expense -> {
                    sum += transaction.amount
                }
                is com.example.data.model.Transaction.Income -> {
                    sum -= transaction.amount
                }
                is com.example.data.model.Transaction.Transfer -> {
                    // Does not influence event cost
                }
            }
        }
        return sum
    }
}