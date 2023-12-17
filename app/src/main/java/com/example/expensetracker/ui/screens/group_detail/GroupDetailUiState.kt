package com.example.expensetracker.ui.screens.group_detail

import com.example.expensetracker.model.Group

sealed class GroupDetailUiState {
    data class Success(val group: Group) :
        GroupDetailUiState()

    data object Loading : GroupDetailUiState()
    data object Error : GroupDetailUiState()
}