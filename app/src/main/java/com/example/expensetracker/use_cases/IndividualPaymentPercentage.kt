package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
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
            eventCost.execute(group.transactions).takeIf { it != 0.0 }
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
            if (result < 0.0) {
                0.0
            } else if (result > 100.0) {
                100.0
            } else {
                result
            }
        }.normalize()
    }

    private fun Map<Participant, Double>.normalize(): Map<Participant, Double> {
        val sum = values.sum()
        return if (sum != 0.0) {
            mapValues { (_, value) -> (value / sum) * 100.0}
        } else {
            Timber.e("Could not normalize values because sum is 0.")
            mapValues { 0.0 }
        }
    }
}