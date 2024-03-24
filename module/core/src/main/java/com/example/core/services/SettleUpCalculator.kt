package com.example.core.services

import android.content.Context
import com.example.core.R
import com.example.core.model.Group
import com.example.core.model.Participant
import com.example.core.model.ParticipantAmount
import com.example.core.model.Transaction
import com.example.core.util.isBiggerThan
import com.example.core.util.isEqualTo
import com.example.core.util.isSmallerThan
import timber.log.Timber
import java.util.Date
import kotlin.math.min

private const val EXPLORATION_DEPTH = 1

interface SettleUpCalculator {
    fun execute(group: Group): List<Transaction.Transfer>
}

internal class SettleUpCalculatorImpl(
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val individualCostsAmount: IndividualCostsAmount,
    private val context: Context
) : SettleUpCalculator {
    override fun execute(group: Group): List<Transaction.Transfer> {
        val payments = individualPaymentAmount.execute(group)
        val costs = individualCostsAmount.execute(group)

        if (payments.map { it.participant }.toSet() != costs.map { it.participant }
                .toSet() || payments.map { it.participant }.toSet() != group.participants.toSet()) {
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
        debtors: List<ParticipantAmount>,
        creditors: List<ParticipantAmount>,
        transactions: List<Transaction.Transfer>
    ): List<Transaction.Transfer> {
        if (debtors.isEmpty() && creditors.isEmpty()) {
            return transactions
        } else if (debtors.isEmpty() || creditors.isEmpty()) {
            Timber.e("Invalid state when balancing up participants. Creditor size is ${creditors.size} and debtors size is ${debtors.size}!")
            return listOf()
        }

        val sortedDebtors =
            debtors.sortedWith(compareBy<ParticipantAmount> { it.amount }.thenBy { it.participant.id })
        val sortedCreditors =
            creditors.sortedWith(compareBy<ParticipantAmount> { it.amount }.thenBy { it.participant.id })

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
        payments: List<ParticipantAmount>,
        costs: List<ParticipantAmount>,
    ) =
        payments.map { payment ->
            ParticipantAmount(participant = payment.participant,
                amount = payments.first { it.participant == payment.participant }.amount - costs.first { it.participant == payment.participant }.amount
            )
        }

    private fun List<ParticipantAmount>.getCreditors() =
        filter { it.amount.isBiggerThan(0.0) }.toList()

    private fun List<ParticipantAmount>.getDebtors() =
        filter { it.amount.isSmallerThan(0.0) }.toList()

    private fun Map<Participant, Double>.toList() =
        map { ParticipantAmount(participant = it.key, amount = it.value) }

    private fun MutableList<ParticipantAmount>.removeIfBalanceZero(index: Int) = this.apply {
        if (this[index].amount.isEqualTo(0.0)) {
            removeAt(index)
        }
    }

    private fun MutableList<Transaction.Transfer>.addSettleUpTransaction(
        debtor: ParticipantAmount,
        creditor: ParticipantAmount
    ): Transaction.Transfer {
        val amount = min(-debtor.amount, creditor.amount)
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

    private fun MutableList<ParticipantAmount>.applyPositiveTransaction(
        index: Int,
        transaction: Transaction
    ) {
        this[index] = this[index].copy(amount = this[index].amount + transaction.amount)
        this.removeIfBalanceZero(index)
    }

    private fun MutableList<ParticipantAmount>.applyNegativeTransaction(
        index: Int,
        transaction: Transaction
    ) {
        this[index] = this[index].copy(amount = this[index].amount - transaction.amount)
        this.removeIfBalanceZero(index)
    }
}