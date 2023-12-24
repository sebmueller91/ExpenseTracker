package com.example.expensetracker.ui.screens.group_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.repositories.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.UUID

class GroupDetailViewModel(
    private val groupId: UUID,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val uiStateFlow = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            when (val group = databaseRepository.getGroup(groupId)) {
                null -> {
                    _uiState.update { GroupDetailUiState.Error }
                }

                else -> {
                    _uiState.update { GroupDetailUiState.Success(group) }
                }
            }
        }
    }
}

fun Transaction.format(currency: Currency): FormattedTransaction {
    // TODO: Move strings into resources
    // TODO: Number formatting has 0 or 2 digits after comma
    val mainText = when (this) {
        is Transaction.Expense -> "${paidBy.name} paid ${
            String.format("%.2f", amount).toDouble()
        }${currency.symbol} for $purpose"

        is Transaction.Income -> "${receivedBy.name} received ${
            String.format("%.2f", amount).toDouble()
        }${currency.symbol} for $purpose"

        is Transaction.Payment -> "${fromParticipant.name} gave ${
            String.format("%.2f", amount).toDouble()
        }${currency.symbol} to ${toParticipant.name} for $purpose"
    }

    val formattedDate = "${SimpleDateFormat("dd.MM.yyyy").format(this.date)}"
    val date = when (this) {
        is Transaction.Expense -> "Paid on: $formattedDate"
        is Transaction.Income -> "Received on: $formattedDate"
        is Transaction.Payment -> "Paid on $formattedDate"
    }

    val splitBetween = when (this) {
        is Transaction.Expense -> "Split between ${splitBetween.joinToString(", ") { it.name }}" // TODO: Move into function and add "and"
        is Transaction.Income -> "Split between ${splitBetween.joinToString(", ") { it.name }}"
        is Transaction.Payment -> "From $fromParticipant to $toParticipant"
    }

    return FormattedTransaction(
        mainText = mainText,
        date = date,
        splitBetween = splitBetween
    )
}