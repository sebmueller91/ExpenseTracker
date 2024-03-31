package com.example.expensetracker.ui.screens.group_overview

import com.example.core.model.SettleUpGroup

sealed class GroupOverviewUiState {
    data class Success(
        val groups: List<SettleUpGroup> = listOf(),
    ) : GroupOverviewUiState()

    data object Loading: GroupOverviewUiState()
}
