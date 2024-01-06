package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction
import timber.log.Timber

interface SettleUp {
    fun execute(group: Group): List<Transaction.Transfer>
}

class SettleUpImpl(
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val individualCostsAmount: IndividualCostsAmount
) : SettleUp {
    override fun execute(group: Group): List<Transaction.Transfer> {
        val payments = individualPaymentAmount.execute(group)
        val costs = individualCostsAmount.execute(group)

        if (payments.keys != costs.keys || payments.keys != group.participants.toSet()) {
            Timber.e("Something is wrong with the participants in the group. Can not calculate settle up payments!")
            return emptyList()
        }

        val balances =
            payments.keys.associateWith { participant -> payments[participant]!! - costs[participant]!! }

        val debtors = balances.filter { it.value < 0 }
        val creditors = balances.filter { it.value > 0 }

        return balanceUpParticipants(debtors = debtors, creditors = creditors)
    }

    private fun balanceUpParticipants(
        debtors: Map<Participant, Double>,
        creditors: Map<Participant, Double>
    ): List<Transaction.Transfer> {
        val transactions = mutableListOf<Transaction.Transfer>()
        val currentDebtors = debtors.toMutableMap()
        val currentCreditors = creditors.toMutableMap()

        while (currentDebtors.any { it.value != 0.0 }) {

        }

        return emptyList()
    }
}