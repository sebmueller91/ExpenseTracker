package com.example.expensetracker.ui.screens.group_detail

import com.example.core.model.Group
import com.example.core.model.ParticipantAmount
import com.example.core.model.ParticipantPercentage
import com.example.core.model.Transaction
import com.example.expensetracker.ui.screens.group_detail.data.FormattedTransaction

sealed class GroupDetailUiState {
    data class Success(
        val group: Group,
        val eventCosts: Double,
        val formattedTransactions: List<FormattedTransaction>,
        val individualShares: List<ParticipantAmount>,
        val percentageShares: List<ParticipantPercentage>,
        val settleUpTransactions: Map<Transaction.Transfer, String>
    ) : GroupDetailUiState()

    data object Loading : GroupDetailUiState()
    data object Error : GroupDetailUiState()
}