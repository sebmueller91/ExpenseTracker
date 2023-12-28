package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction

interface IndividualShareCalculator {
    fun execute(group: Group): Map<Participant, Double>
}

class IndividualShareCalculatorImpl : IndividualShareCalculator {
    override fun execute(group: Group): Map<Participant, Double> {
        return group.participants.associateWith { participant ->
            calculateParticipantsShare(
                participant,
                group.transactions
            )
        }
    }

    private fun calculateParticipantsShare(
        participant: Participant,
        transactions: List<Transaction>
    ): Double {
        var sum = 0.0
        transactions.forEach { transaction ->
            when (transaction) {
                is Transaction.Expense -> {
                    if (transaction.splitBetween.contains(participant)) {
                        sum += transaction.amount / transaction.splitBetween.size.toDouble()
                    }
                }
                is Transaction.Income -> {
                    if (transaction.splitBetween.contains(participant)) {
                        sum -= transaction.amount / transaction.splitBetween.size.toDouble()
                    }
                }
                is Transaction.Payment -> {
                    // Payments do not influence share
                }
            }
        }
        return sum
    }
}