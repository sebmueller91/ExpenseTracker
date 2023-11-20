package com.example.expensetracker.ui.screens.group_overview

import com.example.expensetracker.model.Group

data class GroupOverviewUiState(
    val groups: List<Group> = listOf()
)