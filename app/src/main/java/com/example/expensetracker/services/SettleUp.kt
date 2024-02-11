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
        debtors: List<ParticipantBalance>,
        creditors: List<ParticipantBalance>,
    ): List<Transaction.Transfer> {
        val transactions = mutableListOf<Transaction.Transfer>()

        val mutableDebtorList = debtors.toMutableList()
        val mutableCreditorList = creditors.toMutableList()

        while (mutableDebtorList.isNotEmpty() && mutableCreditorList.isNotEmpty()) {
            mutableDebtorList.sortBy { it.balance }
            mutableCreditorList.sortByDescending { it.balance }

            val currentDebtor = mutableDebtorList.first()
            val currentCreditor = mutableCreditorList.first()

            val amount = min(-currentDebtor.balance, currentCreditor.balance)

            transactions.add(
                Transaction.Transfer(
                    fromParticipant = currentDebtor.participant,
                    toParticipant = currentCreditor.participant,
                    purpose = context.getString(R.string.settle_up),
                    amount = amount,
                    date = Date()
                )
            )

            mutableDebtorList[0] = currentDebtor.copy(balance = currentDebtor.balance + amount)
            mutableCreditorList[0] = currentCreditor.copy(balance = currentCreditor.balance - amount)

            if (mutableDebtorList.first().balance.isEqualTo(0.0)) {
                mutableDebtorList.removeAt(0)
            }
            if (mutableCreditorList.first().balance.isEqualTo(0.0)) {
                mutableCreditorList.removeAt(0)
            }
        }

        return transactions
    }

    private fun calculateBalances(
        payments: Map<Participant, Double>,
        costs: Map<Participant, Double>
    ) =
        payments.keys.associateWith { participant -> payments[participant]!! - costs[participant]!! }

    private fun Map<Participant, Double>.getCreditors() = filter { it.value.isBiggerThan(0.0) }.toList()
    private fun Map<Participant, Double>.getDebtors() = filter { it.value.isSmallerThan(0.0) }.toList()

    private fun Map<Participant, Double>.toList() =
        map { ParticipantBalance(participant = it.key, balance = it.value) }
}

private data class ParticipantBalance(
    val participant: Participant,
    val balance: Double
)