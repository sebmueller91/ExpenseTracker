package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant

interface PercentageShareCalculator {
    fun execute(group: Group): Map<Participant, Double>
}

class PercentageShareCalculatorImpl(
    private val individualShareCalculator: IndividualShareCalculator
) : PercentageShareCalculator {
    override fun execute(group: Group): Map<Participant, Double> {
        TODO("Not yet implemented")
    }

}