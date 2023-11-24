package com.example.expensetracker.ui.screens.add_group

import com.example.expensetracker.model.Currency

data class AddGroupUiState(
    val subScreen: AddGroupSubScreens = AddGroupSubScreens.GROUPNAME_CURRENCY,
    val groupName: String = "",
    val participantsNames: List<String> = listOf(""),
    val currency: Currency = Currency.EURO
)