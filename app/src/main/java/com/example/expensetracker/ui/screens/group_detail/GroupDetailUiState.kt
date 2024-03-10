package com.example.expensetracker.ui.screens.group_detail

import com.example.data.model.Group
import com.example.data.model.Participant
import com.example.data.model.Transaction
import com.example.expensetracker.ui.screens.group_detail.data.FormattedTransaction

sealed class GroupDetailUiState {
    data class Success(
        val group: com.example.data.model.Group,
        val eventCosts: Double,
        val formattedTransactions: List<FormattedTransaction>,
        val individualShares: Map<com.example.data.model.Participant, Double>,
        val percentageShares: Map<com.example.data.model.Participant, Double>,
        val settleUpTransactions: Map<com.example.data.model.Transaction.Transfer, String>
    ) :
        GroupDetailUiState()

    data object Loading : GroupDetailUiState()
    data object Error : GroupDetailUiState()
}