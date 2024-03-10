package com.example.expensetracker.services

import com.example.data.model.Group
import com.example.data.model.Participant
import com.example.data.model.Transaction

interface IndividualCostsAmount {
    fun execute(group: com.example.data.model.Group): Map<com.example.data.model.Participant, Double>
}

class IndividualCostsAmountImpl : IndividualCostsAmount {
    override fun execute(group: com.example.data.model.Group): Map<com.example.data.model.Participant, Double> {
        return group.participants.associateWith { participant ->
            calculateParticipantsCosts(
                participant,
                group.transactions
            )
        }
    }

    private fun calculateParticipantsCosts(
        participant: com.example.data.model.Participant,
        transactions: List<com.example.data.model.Transaction>
    ): Double {
        var sum = 0.0
        transactions.forEach { transaction ->
            when (transaction) {
                is com.example.data.model.Transaction.Expense -> {
                    if (transaction.splitBetween.contains(participant)) {
                        sum += (transaction.amount / transaction.splitBetween.size.toDouble())
                    }
                }

                is com.example.data.model.Transaction.Income -> {
                    if (transaction.splitBetween.contains(participant)) {
                        sum -= (transaction.amount / transaction.splitBetween.size.toDouble())
                    }
                }

                is com.example.data.model.Transaction.Transfer -> {
                    // Does not influence costs
                }
            }
        }
        return sum
    }
}