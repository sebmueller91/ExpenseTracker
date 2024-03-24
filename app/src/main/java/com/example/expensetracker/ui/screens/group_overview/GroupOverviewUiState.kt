package com.example.expensetracker.ui.screens.group_overview

import com.example.core.model.Group

data class GroupOverviewUiState(
    val groups: List<Group> = listOf(),
    val eventCosts: List<String> = listOf()
)