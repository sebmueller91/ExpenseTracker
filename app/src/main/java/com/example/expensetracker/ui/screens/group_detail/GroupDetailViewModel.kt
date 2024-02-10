package com.example.expensetracker.ui.screens.group_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.R
import com.example.expensetracker.data.DatabaseRepository
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.services.EventCosts
import com.example.expensetracker.services.IndividualPaymentAmount
import com.example.expensetracker.services.IndividualPaymentPercentage
import com.example.expensetracker.services.LocaleAwareFormatter
import com.example.expensetracker.services.ResourceResolver
import com.example.expensetracker.services.SettleUp
import com.example.expensetracker.ui.screens.group_detail.data.FormattedTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class GroupDetailViewModel(
    private val groupId: UUID,
    private val databaseRepository: DatabaseRepository,
    private val eventCost: EventCosts,
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val individualPaymentPercentage: IndividualPaymentPercentage,
    private val settleUp: SettleUp,
    private val resourceResolver: ResourceResolver,
    private val localeAwareFormatter: LocaleAwareFormatter
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
                    _uiState.update {
                        GroupDetailUiState.Success(
                            group = group,
                            eventCosts = eventCost.execute(group.transactions),
                            formattedTransactions = group.transactions.map { it.format(group.currency) },
                            individualShares = individualPaymentAmount.execute(group),
                            percentageShares = individualPaymentPercentage.execute(group),
                            settleUpTransactions = settleUp.execute(group)
                                .associateWith { it.formatAsSettleUpTransfer(group.currency) }
                        )
                    }
                }
            }
        }
    }

    fun applySettleUpTransaction(transfer: Transaction.Transfer) {
        databaseRepository.addTransaction(groupId = groupId, transaction = transfer)
    }

    private fun Transaction.Transfer.formatAsSettleUpTransfer(currency: Currency): String {
        return resourceResolver.getString(
            R.string.gives_to,
            fromParticipant.name,
            localeAwareFormatter.formatMoneyAmount(amount, currency),
            toParticipant.name
        )
    }

    private fun Transaction.format(currency: Currency): FormattedTransaction {
        val mainText = when (this) {
            is Transaction.Expense -> resourceResolver.getString(
                R.string.paid_for,
                paidBy.name,
                localeAwareFormatter.formatMoneyAmount(amount, currency),
                purpose
            )

            is Transaction.Income -> resourceResolver.getString(
                R.string.received_for,
                receivedBy.name,
                localeAwareFormatter.formatMoneyAmount(amount, currency),
                purpose
            )

            is Transaction.Transfer -> resourceResolver.getString(
                R.string.gave_to_for,
                fromParticipant.name,
                localeAwareFormatter.formatMoneyAmount(amount, currency),
                toParticipant.name,
                purpose
            )
        }

        val formattedDate = localeAwareFormatter.formatDate(this.date)
        val date = when (this) {
            is Transaction.Expense -> resourceResolver.getString(R.string.paid_on, formattedDate)
            is Transaction.Income -> resourceResolver.getString(R.string.received_on, formattedDate)
            is Transaction.Transfer -> resourceResolver.getString(R.string.paid_on, formattedDate)
        }

        val splitBetween = when (this) {
            is Transaction.Expense -> resourceResolver.getString(
                R.string.split_between,
                localeAwareFormatter.formatParticipantsList(splitBetween)
            )

            is Transaction.Income -> resourceResolver.getString(
                R.string.split_between,
                localeAwareFormatter.formatParticipantsList(splitBetween)
            )

            is Transaction.Transfer -> resourceResolver.getString(
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

    private fun Map<Participant, Double>.sortByValueDescending(): Map<Participant, Double> {
        return toList().sortedByDescending { (_, value) -> value }.toMap()
    }
}