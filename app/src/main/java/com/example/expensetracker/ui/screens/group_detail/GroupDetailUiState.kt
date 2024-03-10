package com.example.expensetracker.ui.screens.group_detail

import com.example.core.model.Group
import com.example.core.model.Participant
import com.example.core.model.Transaction
import com.example.expensetracker.ui.screens.group_detail.data.FormattedTransaction

sealed class GroupDetailUiState {
    data class Success(
        val group: Group,
        val eventCosts: Double,
        val formattedTransactions: List<FormattedTransaction>,
        val individualShares: Map<Participant, Double>,
        val percentageShares: Map<Participant, Double>,
        val settleUpTransactions: Map<Transaction.Transfer, String>
    ) :
        GroupDetailUiState()

    data object Loading : GroupDetailUiState()
    data object Error : GroupDetailUiState()
}