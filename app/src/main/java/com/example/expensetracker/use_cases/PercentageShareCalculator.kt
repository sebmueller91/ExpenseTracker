package com.example.expensetracker.use_cases

import android.util.Log
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant

private const val TAG = "PercentageShareCalculator"

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
                    Log.d(TAG, "No event costs, can not calculate percentage shares.")
                    mapOf() }

        val individualShares = individualShareCalculator.execute(group)

        return group.participants.associateWith { participant ->
            val participantShare = individualShares[participant] ?: return run {
                Log.e(TAG, "Invalid state, could not calculate percentage for user $participant")
                mapOf()
            }

            (participantShare / eventCost) * 100.0
        }
    }
}