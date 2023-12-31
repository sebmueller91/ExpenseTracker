package com.example.expensetracker.ui.di

import com.example.expensetracker.ui.screens.add_group.AddGroupViewModel
import com.example.expensetracker.ui.screens.group_detail.GroupDetailViewModel
import com.example.expensetracker.ui.screens.group_overview.GroupOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.UUID

val uiModule = module {
    viewModel { GroupOverviewViewModel(databaseRepository = get(), eventCostCalculator = get()) }
    viewModel { AddGroupViewModel(databaseRepository = get()) }
    viewModel { (groupId: UUID) ->
        GroupDetailViewModel(
            groupId = groupId,
            databaseRepository = get(),
            eventCostCalculator = get(),
            individualShareCalculator = get(),
            percentageShareCalculator = get()
        )
    }
}