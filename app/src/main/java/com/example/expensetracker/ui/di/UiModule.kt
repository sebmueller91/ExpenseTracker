package com.example.expensetracker.ui.di

import com.example.expensetracker.ui.screens.add_group.AddGroupViewModel
import com.example.expensetracker.ui.screens.group_detail.GroupDetailViewModel
import com.example.expensetracker.ui.screens.group_overview.GroupOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.UUID

val uiModule = module {
    viewModel {
        GroupOverviewViewModel(
            dataRepository = get(),
            eventCost = get(),
            localeAwareFormatter = get()
        )
    }
    viewModel { AddGroupViewModel(dataRepository = get()) }
    viewModel { (groupId: UUID) ->
        GroupDetailViewModel(
            groupId = groupId,
            dataRepository = get(),
            resourceResolver = get(),
            localeAwareFormatter = get()
        )
    }
}