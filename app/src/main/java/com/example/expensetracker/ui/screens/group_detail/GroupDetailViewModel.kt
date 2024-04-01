package com.example.expensetracker.ui.screens.group_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Currency
import com.example.core.model.Transaction
import com.example.core.services.LocaleAwareFormatter
import com.example.core.services.ResourceResolver
import com.example.data.repository.DataRepository
import com.example.expensetracker.R
import com.example.expensetracker.ui.screens.group_detail.data.FormattedSettleUpTransaction
import com.example.expensetracker.ui.screens.group_detail.data.FormattedTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class GroupDetailViewModel(
    private val groupId: UUID,
    private val dataRepository: DataRepository,
    private val resourceResolver: ResourceResolver,
    private val localeAwareFormatter: LocaleAwareFormatter
) : ViewModel() {
    val uiStateFlow: StateFlow<GroupDetailUiState> = dataRepository.groups.map { settleUpGroups ->
        settleUpGroups.firstOrNull { it.group.id == groupId }?.let { settleUpGroup ->
            val group = settleUpGroup.group

            GroupDetailUiState.Success(
                group = group,
                groupCosts = settleUpGroup.groupCosts,
                formattedTransactions = group.transactions.sortedByDescending { it.date }
                    .map { transaction ->
                        transaction.format(group.currency)
                    },
                individualShares = settleUpGroup.individualPaymentAmount,
                percentageShares = settleUpGroup.individualPaymentPercentage,
                settleUpTransactions = settleUpGroup.settleUpTransactions.map {
                    FormattedSettleUpTransaction(
                        transaction = it,
                        formattedText = it.formatAsSettleUpTransfer(group.currency)
                    )
                }
            )
        } ?: GroupDetailUiState.Error
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GroupDetailUiState.Loading
    )

    fun applySettleUpTransaction(transfer: Transaction.Transfer) {
        viewModelScope.launch {
            dataRepository.addTransactionToGroup(groupId = groupId, transaction = transfer)
        }
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
}