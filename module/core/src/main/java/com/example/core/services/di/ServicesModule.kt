package com.example.core.services.di

import com.example.core.services.EventCostsCalculator
import com.example.core.services.EventCostsCalculatorImpl
import com.example.core.services.IndividualCostsAmountCalculator
import com.example.core.services.IndividualCostsAmountCalculatorImpl
import com.example.core.services.IndividualPaymentAmountCalculator
import com.example.core.services.IndividualPaymentAmountCalculatorImpl
import com.example.core.services.IndividualPaymentPercentageCalculator
import com.example.core.services.IndividualPaymentPercentageCalculatorImpl
import com.example.core.services.LocaleAwareFormatter
import com.example.core.services.LocaleAwareFormatterImpl
import com.example.core.services.ResourceResolver
import com.example.core.services.ResourceResolverImpl
import com.example.core.services.SettleUpCalculator
import com.example.core.services.SettleUpCalculatorImpl
import org.koin.dsl.module

val servicesModule = module {
    single<EventCostsCalculator> { EventCostsCalculatorImpl() }
    factory<IndividualCostsAmountCalculator> { IndividualCostsAmountCalculatorImpl() }
    factory<IndividualPaymentAmountCalculator> { IndividualPaymentAmountCalculatorImpl() }
    factory<IndividualPaymentPercentageCalculator> {
        IndividualPaymentPercentageCalculatorImpl(
            eventCost = get(),
            individualPaymentAmountCalculator = get()
        )
    }
    factory<SettleUpCalculator> {
        SettleUpCalculatorImpl(
            individualCostsAmountCalculator = get(),
            individualPaymentAmountCalculator = get(),
            context = get()
        )
    }
    factory<ResourceResolver> { ResourceResolverImpl(context = get()) }
    factory<LocaleAwareFormatter> {
        LocaleAwareFormatterImpl(
            context = get()
        )
    }
}