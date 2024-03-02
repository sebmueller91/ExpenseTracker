package com.example.expensetracker.services.di

import com.example.expensetracker.services.EventCosts
import com.example.expensetracker.services.EventCostsImpl
import com.example.expensetracker.services.IndividualCostsAmount
import com.example.expensetracker.services.IndividualCostsAmountImpl
import com.example.expensetracker.services.IndividualPaymentAmount
import com.example.expensetracker.services.IndividualPaymentAmountImpl
import com.example.expensetracker.services.IndividualPaymentPercentage
import com.example.expensetracker.services.IndividualPaymentPercentageImpl
import com.example.expensetracker.services.LocaleAwareFormatter
import com.example.expensetracker.services.LocaleAwareFormatterImpl
import com.example.expensetracker.services.ResourceResolver
import com.example.expensetracker.services.ResourceResolverImpl
import com.example.expensetracker.services.SettleUp
import com.example.expensetracker.services.SettleUpImpl
import org.koin.dsl.module

val servicesModule = module {
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
            individualPaymentAmount = get(),
            context = get()
        )
    }
    factory<ResourceResolver> { ResourceResolverImpl(context = get()) }
    factory<LocaleAwareFormatter> {LocaleAwareFormatterImpl(context = get()) }
}