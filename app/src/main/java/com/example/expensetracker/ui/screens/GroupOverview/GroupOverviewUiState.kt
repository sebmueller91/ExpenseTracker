package com.example.expensetracker.ui.screens.GroupOverview

import com.example.expensetracker.model.Group

data class GroupOverviewUiState(
    val groups: List<Group> = listOf()
)