package com.example.core.services.di

import com.example.core.services.EventCosts
import com.example.core.services.EventCostsImpl
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
import com.example.core.services.SettleUp
import com.example.core.services.SettleUpImpl
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
    factory<LocaleAwareFormatter> {
        LocaleAwareFormatterImpl(
            context = get()
        )
    }
}