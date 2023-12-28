package com.example.expensetracker.data.di

import com.example.expensetracker.data.DatabaseRepository
import com.example.expensetracker.data.DatabaseRepositoryFakeImpl
import org.koin.dsl.module

val dataModule = module {
    single<DatabaseRepository> { DatabaseRepositoryFakeImpl() }
}