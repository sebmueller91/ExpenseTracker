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

        val balances = calculateBalances(payments, costs)

        val debtors = balances.getDebtors()
        val creditors = balances.getCreditors()

        return balanceUpParticipants(debtors = debtors, creditors = creditors)
    }

    private fun balanceUpParticipants(
        debtors: Map<Participant, Double>,
        creditors: Map<Participant, Double>,
    ): List<Transaction.Transfer> {
        val transactions = mutableListOf<Transaction.Transfer>()

        val debtorList = debtors.map { it.key to it.value }.toMutableList()
        val creditorList = creditors.map { it.key to it.value }.toMutableList()

        while (debtorList.isNotEmpty() && creditorList.isNotEmpty()) {
            debtorList.sortBy { it.second }
            creditorList.sortByDescending { it.second }

            val currentDebtor = debtorList.first()
            val currentCreditor = creditorList.first()

            val amount = min(-currentDebtor.second, currentCreditor.second)

            transactions.add(
                Transaction.Transfer(
                    fromParticipant = currentDebtor.first,
                    toParticipant = currentCreditor.first,
                    purpose = context.getString(R.string.settle_up),
                    amount = amount,
                    date = Date()
                )
            )

            debtorList[0] = currentDebtor.first to (currentDebtor.second + amount)
            creditorList[0] = currentCreditor.first to (currentCreditor.second - amount)

            if (debtorList.first().second.isEqualTo(0.0)) {
                debtorList.removeAt(0)
            }
            if (creditorList.first().second.isEqualTo(0.0)) {
                creditorList.removeAt(0)
            }
        }

        return transactions
    }

    private fun calculateBalances(
        payments: Map<Participant, Double>,
        costs: Map<Participant, Double>
    ) = payments.keys.associateWith { participant -> payments[participant]!! - costs[participant]!! }

    private fun Map<Participant, Double>.getCreditors() = filter { it.value.isBiggerThan(0.0) }
    private fun Map<Participant, Double>.getDebtors() = filter { it.value.isSmallerThan(0.0) }
}