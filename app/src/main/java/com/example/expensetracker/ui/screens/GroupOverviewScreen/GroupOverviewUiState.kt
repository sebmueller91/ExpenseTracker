package com.example.expensetracker.ui.screens.GroupOverviewScreen

import com.example.expensetracker.model.Group

data class GroupOverviewUiState(
    val groups: List<Group> = listOf()
)