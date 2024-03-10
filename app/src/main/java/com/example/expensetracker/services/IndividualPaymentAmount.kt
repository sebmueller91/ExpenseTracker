package com.example.expensetracker.services

import com.example.data.model.Group
import com.example.data.model.Participant
import com.example.data.model.Transaction

interface IndividualPaymentAmount {
    fun execute(group: com.example.data.model.Group): Map<com.example.data.model.Participant, Double>
}

class IndividualPaymentAmountImpl : IndividualPaymentAmount {
    override fun execute(group: com.example.data.model.Group): Map<com.example.data.model.Participant, Double> {
        return group.participants.associateWith { participant ->
            calculateParticipantsPayment(
                participant,
                group.transactions
            )
        }
    }

    private fun calculateParticipantsPayment(
        participant: com.example.data.model.Participant,
        transactions: List<com.example.data.model.Transaction>
    ): Double {
        var sum = 0.0
        transactions.forEach { transaction ->
            when (transaction) {
                is com.example.data.model.Transaction.Expense -> {
                    if (transaction.paidBy == participant ) {
                        sum += transaction.amount
                    }
                }
                is com.example.data.model.Transaction.Income -> {
                    if (transaction.receivedBy == participant) {
                        sum -= transaction.amount
                    }
                }
                is com.example.data.model.Transaction.Transfer -> {
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