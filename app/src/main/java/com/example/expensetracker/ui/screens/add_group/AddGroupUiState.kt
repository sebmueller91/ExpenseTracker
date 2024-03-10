package com.example.expensetracker.ui.screens.add_group

import com.example.data.model.Currency

data class AddGroupUiState(
    val subScreen: AddGroupSubScreens = AddGroupSubScreens.GROUP_NAME_CURRENCY,
    val groupName: String = "",
    val participantsNames: List<String> = listOf(""),
    val currency: com.example.data.model.Currency = com.example.data.model.Currency.EURO
)