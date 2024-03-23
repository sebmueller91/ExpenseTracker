package com.example.core.services

import com.example.core.model.Transaction

interface EventCosts {
    fun execute(transactions: List<Transaction>): Double
}

internal class EventCostsImpl : EventCosts {
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