package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction

interface SettleUp {
    fun execute(group: Group): List<Transaction.Payment>
}

class SettleUpImpl(
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val individualCostsAmount: IndividualCostsAmount
) : SettleUp {
    override fun execute(group: Group): List<Transaction.Payment> {
        TODO("Not yet implemented")
    }

    // TODO: Move into use case
    private fun calculateParticipantsConsumedShares(): Map<Participant, Double> {
        return mapOf()
    }
}