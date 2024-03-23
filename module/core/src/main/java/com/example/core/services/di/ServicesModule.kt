package com.example.core.services.di

import com.example.core.services.EventCostsCalculator
import com.example.core.services.EventCostsCalculatorImpl
import com.example.core.services.IndividualCostsAmount
import com.example.core.services.IndividualCostsAmountImpl
import com.example.core.services.IndividualPaymentAmount
import com.example.core.services.IndividualPaymentAmountImpl
import com.example.core.services.IndividualPaymentPercentage
import com.example.core.services.IndividualPaymentPercentageImpl
import com.example.core.services.LocaleAwareFormatter
import com.example.core.services.LocaleAwareFormatterImpl
import com.example.core.services.ResourceResolver
import com.example.core.services.ResourceResolverImpl
import com.example.core.services.SettleUpCalculator
import com.example.core.services.SettleUpCalculatorImpl
import org.koin.dsl.module

val servicesModule = module {
    single<EventCostsCalculator> { EventCostsCalculatorImpl() }
    factory<IndividualCostsAmount> { IndividualCostsAmountImpl() }
    factory<IndividualPaymentAmount> { IndividualPaymentAmountImpl() }
    factory<IndividualPaymentPercentage> {
        IndividualPaymentPercentageImpl(
            eventCost = get(),
            individualPaymentAmount = get()
        )
    }
    factory<SettleUpCalculator> {
        SettleUpCalculatorImpl(
            individualCostsAmount = get(),
            individualPaymentAmount = get(),
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