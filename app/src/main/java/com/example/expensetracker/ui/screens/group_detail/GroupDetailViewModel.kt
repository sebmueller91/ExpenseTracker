package com.example.expensetracker.ui.screens.group_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.DatabaseRepository
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.use_cases.EventCostCalculator
import com.example.expensetracker.use_cases.IndividualShareCalculator
import com.example.expensetracker.use_cases.PercentageShareCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.UUID

class GroupDetailViewModel(
    private val groupId: UUID,
    private val databaseRepository: DatabaseRepository,
    private val eventCostCalculator: EventCostCalculator,
    private val individualShareCalculator: IndividualShareCalculator,
    private val percentageShareCalculator: PercentageShareCalculator
) : ViewModel() {
    private var _uiState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val uiStateFlow = _uiState.asStateFlow()

    val eventCostsFlow: StateFlow<Double> = _uiState.map {
        when (val uiState = it) {
            GroupDetailUiState.Error,
            GroupDetailUiState.Loading -> 0.0
            is GroupDetailUiState.Success -> eventCostCalculator.execute(uiState.group.transactions)
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = 0.0)

    val individualSharesFlow: StateFlow<Map<Participant, Double>> = _uiState.map {
        when (val uiState = it) {
            GroupDetailUiState.Error,
            GroupDetailUiState.Loading -> emptyMap()
            is GroupDetailUiState.Success -> individualShareCalculator.execute(uiState.group)
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyMap())

    val percentageSharesFlow: StateFlow<Map<Participant, Double>> = _uiState.map {
        when (val uiState = it) {
            GroupDetailUiState.Error,
            GroupDetailUiState.Loading -> emptyMap()
            is GroupDetailUiState.Success -> percentageShareCalculator.execute(uiState.group)
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyMap())

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