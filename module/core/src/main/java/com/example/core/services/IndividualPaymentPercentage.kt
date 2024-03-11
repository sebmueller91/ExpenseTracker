package com.example.core.services

import com.example.core.model.Group
import com.example.core.model.Participant
import com.example.core.util.isBiggerThan
import com.example.core.util.isEqualTo
import com.example.core.util.isSmallerThan
import timber.log.Timber

interface IndividualPaymentPercentage {
    fun execute(group: Group): Map<Participant, Double>
}

class IndividualPaymentPercentageImpl(
    private val eventCost: EventCosts,
    private val individualPaymentAmount: IndividualPaymentAmount
) : IndividualPaymentPercentage {
    override fun execute(group: Group): Map<Participant, Double> {
        val eventCost =
            eventCost.execute(group.transactions).takeIf { !it.isEqualTo(0.0) }
                ?: return run {
                    Timber.d("No event costs, can not calculate percentage shares.")
                    mapOf()
                }

        val individualShares = individualPaymentAmount.execute(group)

        return group.participants.associateWith { participant ->
            val participantShare = individualShares[participant] ?: return run {
                Timber.e("Invalid state, could not calculate percentage for user $participant")
                mapOf()
            }

            val result = (participantShare / eventCost) * 100.0
            if (result.isSmallerThan(0.0)) {
                0.0
            } else if (result.isBiggerThan(100.0)) {
                100.0
            } else {
                result
            }
        }.normalize()
    }

    private fun Map<Participant, Double>.normalize(): Map<Participant, Double> {
        val sum = values.sum()
        return if (!sum.isEqualTo(0.0)) {
            mapValues { (_, value) -> (value / sum) * 100.0}
        } else {
            Timber.e("Could not normalize values because sum is 0.")
            mapValues { 0.0 }
        }
    }
}