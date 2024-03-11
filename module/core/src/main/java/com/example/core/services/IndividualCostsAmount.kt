package com.example.core.services

import com.example.core.model.Group
import com.example.core.model.Participant
import com.example.core.model.Transaction

interface IndividualCostsAmount {
    fun execute(group: Group): Map<Participant, Double>
}

class IndividualCostsAmountImpl : IndividualCostsAmount {
    override fun execute(group: Group): Map<Participant, Double> {
        return group.participants.associateWith { participant ->
            calculateParticipantsCosts(
                participant,
                group.transactions
            )
        }
    }

    private fun calculateParticipantsCosts(
        participant: Participant,
        transactions: List<Transaction>
    ): Double {
        var sum = 0.0
        transactions.forEach { transaction ->
            when (transaction) {
                is Transaction.Expense -> {
                    if (transaction.splitBetween.contains(participant)) {
                        sum += (transaction.amount / transaction.splitBetween.size.toDouble())
                    }
                }

                is Transaction.Income -> {
                    if (transaction.splitBetween.contains(participant)) {
                        sum -= (transaction.amount / transaction.splitBetween.size.toDouble())
                    }
                }

                is Transaction.Transfer -> {
                    // Does not influence costs
                }
            }
        }
        return sum
    }
}