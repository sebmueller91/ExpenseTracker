package com.example.expensetracker.ui.screens.group_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Currency
import com.example.core.model.Participant
import com.example.core.model.Transaction
import com.example.core.services.EventCosts
import com.example.core.services.IndividualPaymentAmount
import com.example.core.services.IndividualPaymentPercentage
import com.example.core.services.LocaleAwareFormatter
import com.example.core.services.ResourceResolver
import com.example.expensetracker.R
import com.example.expensetracker.ui.screens.group_detail.data.FormattedTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class GroupDetailViewModel(
    private val groupId: UUID,
    private val dataRepository: com.example.data.repository.DataRepository,
    private val eventCost: EventCosts,
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val individualPaymentPercentage: IndividualPaymentPercentage,
    private val resourceResolver: ResourceResolver,
    private val localeAwareFormatter: LocaleAwareFormatter
) : ViewModel() {
    val uiStateFlow: StateFlow<GroupDetailUiState> = dataRepository.groups
        .map { settleUpGroups ->
            settleUpGroups.firstOrNull { settleUpGroup -> settleUpGroup.group.id == groupId }
        }
        .map { settleUpGroup ->
            when (settleUpGroup) {
                null -> GroupDetailUiState.Error
                else -> GroupDetailUiState.Success(
                    group = settleUpGroup.group,
                    eventCosts = eventCost.execute(settleUpGroup.group.transactions),
                    formattedTransactions = settleUpGroup.group.transactions.map {
                        it.format(
                            settleUpGroup.group.currency
                        )
                    },
                    individualShares = individualPaymentAmount.execute(settleUpGroup.group)
                        .sortByValueDesc(),
                    percentageShares = individualPaymentPercentage.execute(settleUpGroup.group)
                        .sortByValueDesc(),
                    settleUpTransactions = settleUpGroup.settleUpTransactions
                        .associateWith { it.formatAsSettleUpTransfer(settleUpGroup.group.currency) }
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, GroupDetailUiState.Loading)

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

    private fun Map<Participant, Double>.sortByValueDesc(): Map<Participant, Double> =
        toList().sortedByDescending { it.second }.toMap()
}