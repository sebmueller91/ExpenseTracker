package com.example.expensetracker.ui.screens.group_overview

import com.example.data.model.Group

data class GroupOverviewUiState(
    val groups: List<com.example.data.model.Group> = listOf(),
    val eventCosts: List<String> = listOf()
)