package com.example.core.services

import com.example.core.model.Group
import com.example.core.model.ParticipantPercentage
import com.example.core.util.isBiggerThan
import com.example.core.util.isEqualTo
import com.example.core.util.isSmallerThan
import timber.log.Timber

interface IndividualPaymentPercentageCalculator {
    fun execute(group: Group): List<ParticipantPercentage>
}

internal class IndividualPaymentPercentageCalculatorImpl(
    private val groupCostCalculator: GroupCostsCalculator,
    private val individualPaymentAmountCalculator: IndividualPaymentAmountCalculator
) : IndividualPaymentPercentageCalculator {
    override fun execute(group: Group): List<ParticipantPercentage> {
        val groupCosts =
            groupCostCalculator.execute(group.transactions).takeIf { !it.isEqualTo(0.0) }
                ?: return run {
                    Timber.d("No event costs, can not calculate percentage shares.")
                    listOf()
                }

        val individualShares = individualPaymentAmountCalculator.execute(group)

        return group.participants.map { participant ->
            val participantShare =
                individualShares.firstOrNull { it.participant == participant } ?: return run {
                    Timber.e("Invalid state, could not calculate percentage for user $participant")
                    listOf()
                }

            val result = (participantShare.amount / groupCosts) * 100.0
            ParticipantPercentage(
                participant,

                when {
                    result.isSmallerThan(0.0) -> {
                        0.0
                    }
                    result.isBiggerThan(100.0) -> {
                        100.0
                    }
                    else -> {
                        result
                    }
                }
            )
        }.normalize().sortedByDescending { it.percentage }
    }

    private fun List<ParticipantPercentage>.normalize(): List<ParticipantPercentage> {
        val sum = sumOf { it.percentage }

        return map {
            ParticipantPercentage(
                participant = it.participant,
                percentage = when {
                    !sum.isEqualTo(0.0) -> (it.percentage / sum) * 100.0
                    else -> 0.0

                }
            )
        }
    }
}