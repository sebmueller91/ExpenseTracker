package com.example.expensetracker.ui.screens.group_overview

import com.example.core.model.Group

sealed class GroupOverviewUiState {
    data class Success(
        val groups: List<Pair<Group, Double>> = listOf(),
    ) : GroupOverviewUiState()

    data object Loading: GroupOverviewUiState()
}
