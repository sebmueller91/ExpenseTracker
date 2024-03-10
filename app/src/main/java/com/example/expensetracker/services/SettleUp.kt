package com.example.expensetracker.services

import android.content.Context
import com.example.expensetracker.R
import com.example.core.model.Group
import com.example.core.model.Participant
import com.example.core.model.Transaction
import com.example.core.util.isBiggerThan
import com.example.core.util.isEqualTo
import com.example.core.util.isSmallerThan
import timber.log.Timber
import java.util.Date
import kotlin.math.min

private const val EXPLORATION_DEPTH = 1

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

        return balanceUpParticipants(
            debtors = debtors,
            creditors = creditors,
            transactions = listOf()
        )
    }

    private fun balanceUpParticipants(
        debtors: List<ParticipantBalance>,
        creditors: List<ParticipantBalance>,
        transactions: List<Transaction.Transfer>
    ): List<Transaction.Transfer> {
        if (debtors.isEmpty() && creditors.isEmpty()) {
            return transactions
        } else if (debtors.isEmpty() || creditors.isEmpty()) {
            Timber.e("Invalid state when balancing up participants. Creditor size is ${creditors.size} and debtors size is ${debtors.size}!")
            return listOf()
        }

        val sortedDebtors =
            debtors.sortedWith(compareBy<ParticipantBalance> { it.balance }.thenBy { it.participant.id })
        val sortedCreditors =
            creditors.sortedWith(compareBy<ParticipantBalance> { it.balance }.thenBy { it.participant.id })

        val transactionOptions = mutableListOf<List<Transaction.Transfer>>()
        sortedDebtors.take(EXPLORATION_DEPTH).forEachIndexed { debtorIndex, debtor ->
            sortedCreditors.take(EXPLORATION_DEPTH).forEachIndexed { creditorIndex, creditor ->
                val currentTransactions = transactions.toMutableList()
                val currentDebtors = sortedDebtors.toMutableList()
                val currentCreditors = sortedCreditors.toMutableList()

                val transaction =
                    currentTransactions.addSettleUpTransaction(debtor = debtor, creditor = creditor)
                currentDebtors.applyPositiveTransaction(debtorIndex, transaction)
                currentCreditors.applyNegativeTransaction(creditorIndex, transaction)

                transactionOptions.add(
                    balanceUpParticipants(
                        debtors = currentDebtors,
                        creditors = currentCreditors,
                        transactions = currentTransactions
                    )
                )
            }
        }
        return transactionOptions.minBy { it.size }
    }

    private fun calculateBalances(
        payments: Map<Participant, Double>,
        costs: Map<Participant, Double>
    ) =
        payments.keys.associateWith { participant -> payments[participant]!! - costs[participant]!! }

    private fun Map<Participant, Double>.getCreditors() =
        filter { it.value.isBiggerThan(0.0) }.toList()

    private fun Map<Participant, Double>.getDebtors() =
        filter { it.value.isSmallerThan(0.0) }.toList()

    private fun Map<Participant, Double>.toList() =
        map { ParticipantBalance(participant = it.key, balance = it.value) }

    private fun MutableList<ParticipantBalance>.removeIfBalanceZero(index: Int) = this.apply {
        if (this[index].balance.isEqualTo(0.0)) {
            removeAt(index)
        }
    }

    private fun MutableList<Transaction.Transfer>.addSettleUpTransaction(
        debtor: ParticipantBalance,
        creditor: ParticipantBalance
    ): Transaction.Transfer {
        val amount = min(-debtor.balance, creditor.balance)
        add(
            Transaction.Transfer(
                fromParticipant = debtor.participant,
                toParticipant = creditor.participant,
                purpose = context.getString(R.string.settle_up),
                amount = amount,
                date = Date()
            )
        )
        return last()
    }

    private fun MutableList<ParticipantBalance>.applyPositiveTransaction(
        index: Int,
        transaction: Transaction
    ) {
        this[index] = this[index].copy(balance = this[index].balance + transaction.amount)
        this.removeIfBalanceZero(index)
    }

    private fun MutableList<ParticipantBalance>.applyNegativeTransaction(
        index: Int,
        transaction: Transaction
    ) {
        this[index] = this[index].copy(balance = this[index].balance - transaction.amount)
        this.removeIfBalanceZero(index)
    }
}

private data class ParticipantBalance(
    val participant: Participant,
    val balance: Double
)