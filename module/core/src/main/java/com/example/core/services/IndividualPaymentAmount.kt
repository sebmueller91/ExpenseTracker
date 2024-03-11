package com.example.core.services

import com.example.core.model.Group
import com.example.core.model.Participant
import com.example.core.model.Transaction

interface IndividualPaymentAmount {
    fun execute(group: Group): Map<Participant, Double>
}

class IndividualPaymentAmountImpl : IndividualPaymentAmount {
    override fun execute(group: Group): Map<Participant, Double> {
        return group.participants.associateWith { participant ->
            calculateParticipantsPayment(
                participant,
                group.transactions
            )
        }
    }

    private fun calculateParticipantsPayment(
        participant: Participant,
        transactions: List<Transaction>
    ): Double {
        var sum = 0.0
        transactions.forEach { transaction ->
            when (transaction) {
                is Transaction.Expense -> {
                    if (transaction.paidBy == participant ) {
                        sum += transaction.amount
                    }
                }
                is Transaction.Income -> {
                    if (transaction.receivedBy == participant) {
                        sum -= transaction.amount
                    }
                }
                is Transaction.Transfer -> {
                    if (transaction.fromParticipant == participant) {
                        sum += transaction.amount
                    }
                    if (transaction.toParticipant == participant) {
                        sum -= transaction.amount
                    }
                }
            }
        }
        return sum
    }
}