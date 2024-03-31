package com.example.core.model

data class SettleUpGroup(
    val group: Group,
    val settleUpTransactions: List<Transaction.Transfer>,
    val groupCosts: Double,
    val individualPaymentAmount: List<ParticipantAmount>,
    val individualPaymentPercentage: List<ParticipantPercentage>
)