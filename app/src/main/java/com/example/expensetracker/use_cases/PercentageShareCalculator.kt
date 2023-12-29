package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import timber.log.Timber
interface PercentageShareCalculator {
    fun execute(group: Group): Map<Participant, Double>
}

class PercentageShareCalculatorImpl(
    private val eventCostCalculator: EventCostCalculator,
    private val individualShareCalculator: IndividualShareCalculator
) : PercentageShareCalculator {
    override fun execute(group: Group): Map<Participant, Double> {
        val eventCost =
            eventCostCalculator.execute(group.transactions).takeIf { it != 0.0 }
                ?: return run {
                    Timber.d("No event costs, can not calculate percentage shares.")
                    mapOf() }

        val individualShares = individualShareCalculator.execute(group)

        return group.participants.associateWith { participant ->
            val participantShare = individualShares[participant] ?: return run {
                Timber.e("Invalid state, could not calculate percentage for user $participant")
                mapOf()
            }

            (participantShare / eventCost) * 100.0
        }
    }
}