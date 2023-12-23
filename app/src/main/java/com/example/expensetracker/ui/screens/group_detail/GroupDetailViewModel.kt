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
    val mainText = when (this) {
        is Transaction.Expense -> {
            "${paidBy.name} paid ${String.format("%.2f", amount).toDouble()}${currency.symbol} for $purpose"
        }
        is Transaction.Income -> "TODO()"
        is Transaction.Payment -> "TODO()"
    }

    val date = when (this) {
        is Transaction.Expense -> "Paid on: ${SimpleDateFormat("dd.MM.yyyy").format(this.date)}"
        is Transaction.Income -> "TODO()"
        is Transaction.Payment -> "TODO()"
    }

    val splitBetween = when (this) {
        is Transaction.Expense -> "Split between ${splitBetween.joinToString(", ") { it.name }}"
        is Transaction.Income -> "TODO()"
        is Transaction.Payment -> "TODO()"
    }

    return FormattedTransaction(
        mainText = mainText,
        date = date,
        splitBetween = splitBetween
    )
}