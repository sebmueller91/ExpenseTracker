package com.example.expensetracker.di

import com.example.expensetracker.repositories.DatabaseRepository
import com.example.expensetracker.repositories.DatabaseRepositoryFakeImpl
import com.example.expensetracker.ui.screens.GroupOverviewScreen.GroupOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<DatabaseRepository> {DatabaseRepositoryFakeImpl()}
    viewModel { GroupOverviewViewModel(databaseRepository = get()) }
}