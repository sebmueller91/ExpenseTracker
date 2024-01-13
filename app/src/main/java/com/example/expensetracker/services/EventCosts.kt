package com.example.expensetracker.services

import com.example.expensetracker.model.Transaction

interface EventCosts {
    fun execute(transactions: List<Transaction>): Double
}

class EventCostsImpl : EventCosts {
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
                is Transaction.Transfer -> {
                    // Does not influence event cost
                }
            }
        }
        return sum
    }
}