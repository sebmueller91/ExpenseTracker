package com.example.expensetracker.ui.screens.group_detail.data

import com.example.core.model.Transaction

data class FormattedSettleUpTransaction(
    val transaction: Transaction.Transfer,
    val formattedText: String
)