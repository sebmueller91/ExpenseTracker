package com.example.expensetracker.ui.screens.groupOverview

import com.example.expensetracker.model.Group

data class GroupOverviewUiState(
    val groups: List<Group> = listOf()
)