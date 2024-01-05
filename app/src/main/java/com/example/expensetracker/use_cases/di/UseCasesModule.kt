package com.example.expensetracker.use_cases.di

import com.example.expensetracker.use_cases.EventCosts
import com.example.expensetracker.use_cases.EventCostsImpl
import com.example.expensetracker.use_cases.IndividualCostsAmount
import com.example.expensetracker.use_cases.IndividualCostsAmountImpl
import com.example.expensetracker.use_cases.IndividualPaymentAmount
import com.example.expensetracker.use_cases.IndividualPaymentAmountImpl
import com.example.expensetracker.use_cases.IndividualPaymentPercentage
import com.example.expensetracker.use_cases.IndividualPaymentPercentageImpl
import com.example.expensetracker.use_cases.SettleUp
import com.example.expensetracker.use_cases.SettleUpImpl
import org.koin.dsl.module

val useCasesModule = module {
    single<EventCosts> { EventCostsImpl() }
    factory<IndividualCostsAmount> { IndividualCostsAmountImpl() }
    factory<IndividualPaymentAmount> { IndividualPaymentAmountImpl() }
    factory<IndividualPaymentPercentage> {
        IndividualPaymentPercentageImpl(
            eventCost = get(),
            individualPaymentAmount = get()
        )
    }
    factory<SettleUp> {
        SettleUpImpl(
            individualCostsAmount = get(),
            individualPaymentAmount = get()
        )
    }
}