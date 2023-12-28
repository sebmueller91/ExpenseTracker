package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant

interface IndividualShareCalculator {
    fun execute(group: Group): Map<Participant, Double>
}

class IndividualShareCalculatorImpl(
    private val eventCostCalculator: EventCostCalculator
) : IndividualShareCalculator {
    override fun execute(group: Group): Map<Participant, Double> {
        TODO("Not yet implemented")
    }
}