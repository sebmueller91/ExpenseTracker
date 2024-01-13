package com.example.expensetracker.ui.screens.group_detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.R
import com.example.expensetracker.data.DatabaseRepository
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.util.UiUtils
import com.example.expensetracker.services.EventCosts
import com.example.expensetracker.services.IndividualPaymentAmount
import com.example.expensetracker.services.IndividualPaymentPercentage
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
    private val eventCost: EventCosts,
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val percentageShareCalculator: IndividualPaymentPercentage
) : ViewModel() {
    private var _uiState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val uiStateFlow = _uiState.asStateFlow()

    val eventCostsFlow: StateFlow<Double> = _uiState.map {
        when (val uiState = it) {
            GroupDetailUiState.Error,
            GroupDetailUiState.Loading -> 0.0

            is GroupDetailUiState.Success -> eventCost.execute(uiState.group.transactions)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0.0
    )

    val individualSharesFlow: StateFlow<Map<Participant, Double>> = _uiState.map {
        when (val uiState = it) {
            GroupDetailUiState.Error,
            GroupDetailUiState.Loading -> emptyMap()

            is GroupDetailUiState.Success -> individualPaymentAmount.execute(uiState.group)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap()
    )

    val percentageSharesFlow: StateFlow<Map<Participant, Double>> = _uiState.map {
        when (val uiState = it) {
            GroupDetailUiState.Error,
            GroupDetailUiState.Loading -> emptyMap()

            is GroupDetailUiState.Success -> percentageShareCalculator.execute(uiState.group)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap()
    )

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

fun Transaction.format(currency: Currency, context: Context): FormattedTransaction {
    val mainText = when (this) {
        is Transaction.Expense -> context.getString(
            R.string.paid_for,
            paidBy.name,
            UiUtils.formatMoneyAmount(amount, currency, context),
            purpose
        )

        is Transaction.Income -> context.getString(
            R.string.received_for,
            receivedBy.name,
            UiUtils.formatMoneyAmount(amount, currency, context),
            purpose
        )

        is Transaction.Transfer -> context.getString(
            R.string.gave_to_for,
            fromParticipant.name,
            UiUtils.formatMoneyAmount(amount, currency, context),
            toParticipant.name,
            purpose
        )
    }

    val formattedDate = "${SimpleDateFormat("dd.MM.yyyy").format(this.date)}"
    val date = when (this) {
        is Transaction.Expense -> context.getString(R.string.paid_on, formattedDate)
        is Transaction.Income -> context.getString(R.string.received_on, formattedDate)
        is Transaction.Transfer -> context.getString(R.string.paid_on, formattedDate)
    }

    val splitBetween = when (this) {
        is Transaction.Expense -> context.getString(
            R.string.split_between,
            UiUtils.formatParticipantsList(splitBetween, context))
        is Transaction.Income -> context.getString(
            R.string.split_between,
            UiUtils.formatParticipantsList(splitBetween, context))
        is Transaction.Transfer -> context.getString(
            R.string.from_to,
            fromParticipant.name,
            toParticipant.name
        )
    }

    return FormattedTransaction(
        mainText = mainText,
        date = date,
        splitBetween = splitBetween
    )
}