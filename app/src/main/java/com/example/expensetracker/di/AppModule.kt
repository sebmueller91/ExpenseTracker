package com.example.expensetracker.di

import com.example.expensetracker.repositories.DatabaseRepository
import com.example.expensetracker.repositories.DatabaseRepositoryFakeImpl
import com.example.expensetracker.ui.screens.add_group.AddGroupViewModel
import com.example.expensetracker.ui.screens.group_detail.GroupDetailViewModel
import com.example.expensetracker.ui.screens.group_overview.GroupOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.UUID

val appModule = module {
    single<DatabaseRepository> { DatabaseRepositoryFakeImpl() }
    viewModel { GroupOverviewViewModel(databaseRepository = get()) }
    viewModel { AddGroupViewModel(databaseRepository = get()) }
    viewModel { (groupId: UUID) -> GroupDetailViewModel(groupId = groupId, databaseRepository = get())}
}