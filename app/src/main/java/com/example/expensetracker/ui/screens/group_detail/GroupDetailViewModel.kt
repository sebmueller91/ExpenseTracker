package com.example.expensetracker.ui.screens.group_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.R
import com.example.data.repository.DatabaseRepository
import com.example.data.model.Currency
import com.example.data.model.Participant
import com.example.data.model.Transaction
import com.example.expensetracker.services.EventCosts
import com.example.expensetracker.services.IndividualPaymentAmount
import com.example.expensetracker.services.IndividualPaymentPercentage
import com.example.expensetracker.services.LocaleAwareFormatter
import com.example.expensetracker.services.ResourceResolver
import com.example.expensetracker.services.SettleUp
import com.example.expensetracker.ui.screens.group_detail.data.FormattedTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class GroupDetailViewModel(
    private val groupId: UUID,
    private val databaseRepository: com.example.data.repository.DatabaseRepository,
    private val eventCost: EventCosts,
    private val individualPaymentAmount: IndividualPaymentAmount,
    private val individualPaymentPercentage: IndividualPaymentPercentage,
    private val settleUp: SettleUp,
    private val resourceResolver: ResourceResolver,
    private val localeAwareFormatter: LocaleAwareFormatter
) : ViewModel() {
    val uiStateFlow: StateFlow<GroupDetailUiState> = databaseRepository.groups.map {
        it.firstOrNull { it.id == groupId }
    }.map { group ->
        when (group) {
            null -> GroupDetailUiState.Error
            else -> GroupDetailUiState.Success(
                group = group,
                eventCosts = eventCost.execute(group.transactions),
                formattedTransactions = group.transactions.map { it.format(group.currency) },
                individualShares = individualPaymentAmount.execute(group).sortByValueDesc(),
                percentageShares = individualPaymentPercentage.execute(group).sortByValueDesc(),
                settleUpTransactions = settleUp.execute(group)
                    .associateWith { it.formatAsSettleUpTransfer(group.currency) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GroupDetailUiState.Loading)

    fun applySettleUpTransaction(transfer: com.example.data.model.Transaction.Transfer) {
        databaseRepository.addTransaction(groupId = groupId, transaction = transfer)
    }

    private fun com.example.data.model.Transaction.Transfer.formatAsSettleUpTransfer(currency: com.example.data.model.Currency): String {
        return resourceResolver.getString(
            R.string.gives_to,
            fromParticipant.name,
            localeAwareFormatter.formatMoneyAmount(amount, currency),
            toParticipant.name
        )
    }

    private fun com.example.data.model.Transaction.format(currency: com.example.data.model.Currency): FormattedTransaction {
        val mainText = when (this) {
            is com.example.data.model.Transaction.Expense -> resourceResolver.getString(
                R.string.paid_for,
                paidBy.name,
                localeAwareFormatter.formatMoneyAmount(amount, currency),
                purpose
            )

            is com.example.data.model.Transaction.Income -> resourceResolver.getString(
                R.string.received_for,
                receivedBy.name,
                localeAwareFormatter.formatMoneyAmount(amount, currency),
                purpose
            )

            is com.example.data.model.Transaction.Transfer -> resourceResolver.getString(
                R.string.gave_to_for,
                fromParticipant.name,
                localeAwareFormatter.formatMoneyAmount(amount, currency),
                toParticipant.name,
                purpose
            )
        }

        val formattedDate = localeAwareFormatter.formatDate(this.date)
        val date = when (this) {
            is com.example.data.model.Transaction.Expense -> resourceResolver.getString(R.string.paid_on, formattedDate)
            is com.example.data.model.Transaction.Income -> resourceResolver.getString(R.string.received_on, formattedDate)
            is com.example.data.model.Transaction.Transfer -> resourceResolver.getString(R.string.paid_on, formattedDate)
        }

        val splitBetween = when (this) {
            is com.example.data.model.Transaction.Expense -> resourceResolver.getString(
                R.string.split_between,
                localeAwareFormatter.formatParticipantsList(splitBetween)
            )

            is com.example.data.model.Transaction.Income -> resourceResolver.getString(
                R.string.split_between,
                localeAwareFormatter.formatParticipantsList(splitBetween)
            )

            is com.example.data.model.Transaction.Transfer -> resourceResolver.getString(
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

    private fun Map<com.example.data.model.Participant, Double>.sortByValueDesc(): Map<com.example.data.model.Participant, Double> =
        toList().sortedByDescending { it.second }.toMap()
}