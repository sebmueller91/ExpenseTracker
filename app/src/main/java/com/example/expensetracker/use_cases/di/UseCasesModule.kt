package com.example.expensetracker.use_cases.di

import com.example.expensetracker.use_cases.EventCostCalculator
import com.example.expensetracker.use_cases.EventCostCalculatorImpl
import com.example.expensetracker.use_cases.IndividualShareCalculator
import com.example.expensetracker.use_cases.IndividualShareCalculatorImpl
import com.example.expensetracker.use_cases.PercentageShareCalculator
import com.example.expensetracker.use_cases.PercentageShareCalculatorImpl
import org.koin.dsl.module

val useCasesModule = module {
    single<EventCostCalculator> { EventCostCalculatorImpl() }
    factory<IndividualShareCalculator> { IndividualShareCalculatorImpl() }
    factory<PercentageShareCalculator> {
        PercentageShareCalculatorImpl(
            eventCostCalculator = get(),
            individualShareCalculator = get()
        )
    }
}