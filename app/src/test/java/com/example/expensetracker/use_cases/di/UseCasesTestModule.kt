package com.example.expensetracker.use_cases.di

import com.example.expensetracker.use_cases.EventCostCalculator
import com.example.expensetracker.use_cases.EventCostCalculatorImpl
import org.koin.dsl.module

val useCasesTestModule = module {
    single<EventCostCalculator> { EventCostCalculatorImpl() }
}