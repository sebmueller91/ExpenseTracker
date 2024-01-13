package com.example.expensetracker.services

import android.content.Context
import com.example.expensetracker.R
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.util.isBiggerThan
import com.example.expensetracker.util.isEqualTo
import com.example.expensetracker.util.isSmallerThan
import timber.log.Timber
import java.util.Date
import kotlin.math.min

interface SettleUp {
    fun execute(group: Group): List<Transaction.Transfer>
}

class SettleUpImpl(
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val individualCostsAmount: IndividualCostsAmount,
    private val context: Context
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

        val debtors = balances.filter { it.value.isSmallerThan(0.0)}
        val creditors = balances.filter { it.value.isBiggerThan(0.0) }

        return balanceUpParticipants(debtors = debtors, creditors = creditors, context = context)
    }

    private fun balanceUpParticipants(
        debtors: Map<Participant, Double>,
        creditors: Map<Participant, Double>,
        context: Context
    ): List<Transaction.Transfer> {
        val transactions = mutableListOf<Transaction.Transfer>()

        val sortedDebtors = debtors.entries.sortedBy { it.value }
        val sortedCreditors = creditors.entries.sortedByDescending { it.value }

        val sortedDebtorsBalances = sortedDebtors.map { it.value }.toMutableList()
        val sortedCreditorsBalances = sortedCreditors.map { it.value }.toMutableList()

        var debtorIndex = 0
        var creditorIndex = 0

        while (debtorIndex < sortedDebtors.size && creditorIndex < sortedCreditors.size) {
            val debtor = sortedDebtors[debtorIndex]
            val creditor = sortedCreditors[creditorIndex]

            val amount = min (-sortedDebtorsBalances[debtorIndex], sortedCreditorsBalances[creditorIndex])
            transactions.add(Transaction.Transfer(
                fromParticipant = debtor.key,
                toParticipant = creditor.key,
                purpose = context.getString(R.string.settle_up),
                amount = amount,
                date = Date()
            ))

            sortedDebtorsBalances[debtorIndex] +=amount
            sortedCreditorsBalances[creditorIndex] -=amount

            if (sortedDebtorsBalances[debtorIndex].isEqualTo(0.0)) {
                debtorIndex++
            }
            if (sortedCreditorsBalances[creditorIndex].isEqualTo(0.0)) {
                creditorIndex++
            }
        }

        return transactions
    }
}