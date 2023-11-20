package com.example.expensetracker.ui.screens.add_group

import com.example.expensetracker.model.Currency

data class AddGroupUiState(
    val showScreen1: Boolean = true, // TODO: Better name?
    val groupName: String = "",
    val participantsNames: List<String> = listOf(""),
    val currency: Currency = Currency.EURO
)