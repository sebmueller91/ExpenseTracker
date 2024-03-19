package com.example.expensetracker.ui.screens.group_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Currency
import com.example.core.model.Group
import com.example.core.services.EventCosts
import com.example.core.services.LocaleAwareFormatter
import com.example.core.util.FakeData
import com.example.data.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupOverviewViewModel(
    private val dataRepository: DataRepository,
    private val eventCost: EventCosts,
    private val localeAwareFormatter: LocaleAwareFormatter
) : ViewModel() {
    private var _uiState = MutableStateFlow(GroupOverviewUiState())
    val uiStateFlow = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataRepository.groups.collect { settleUpGroups ->
                _uiState.update {
                    GroupOverviewUiState(
                        groups = settleUpGroups.map { settleUpGroup -> settleUpGroup.group },
                        eventCosts = settleUpGroups.map { settleUpGroup -> settleUpGroup.group.formattedEventCosts() })
                }
            }
        }
    }

    fun addFakeData() {
        viewModelScope.launch {
            dataRepository.addGroup(
                Group(
                    currency = Currency.EURO, name = "Rock im Park",
                    participants = FakeData.fakeParticipantsBig,
                    transactions = listOf(
                        FakeData.createFakeExpense(FakeData.fakeParticipantsBig, amount = 100.0),
                        FakeData.createFakeExpense(FakeData.fakeParticipantsBig, amount = 100.90),
                        FakeData.createFakeExpense(FakeData.fakeParticipantsBig, amount = 100.10),
                        FakeData.createFakeExpense(FakeData.fakeParticipantsBig, amount = 100.23),
                        FakeData.createFakeExpense(FakeData.fakeParticipantsBig, amount = 100.0),
                        FakeData.createFakePayment(FakeData.fakeParticipantsBig, amount = 100.0),
                        FakeData.createFakePayment(FakeData.fakeParticipantsBig, amount = 100.0),
                        FakeData.createFakePayment(FakeData.fakeParticipantsBig, amount = 100.0),
                        FakeData.createFakePayment(FakeData.fakeParticipantsBig, amount = 100.0),
                        FakeData.createFakePayment(FakeData.fakeParticipantsBig, amount = 22.0),
                    )
                )
            )
            dataRepository.addGroup(
                Group(
                    currency = Currency.USD, name = "Summer Breeze",
                    participants = (FakeData.fakeParticipantsSmall),
                    transactions = listOf(
                        FakeData.createFakeExpense(
                            FakeData.fakeParticipantsSmall,
                            amount = 100.0
                        ),
                        FakeData.createFakeIncome(
                            FakeData.fakeParticipantsSmall,
                            amount = 10.0
                        ),
                        FakeData.createFakePayment(
                            FakeData.fakeParticipantsSmall,
                            amount = 20.0
                        ),
                        FakeData.createFakeExpense(
                            FakeData.fakeParticipantsSmall,
                            amount = 100.24
                        ),
                        FakeData.createFakeExpense(
                            FakeData.fakeParticipantsSmall,
                            amount = 100.20
                        ),
                        FakeData.createFakeExpense(
                            FakeData.fakeParticipantsSmall,
                            amount = 100.0
                        ),
                        FakeData.createFakeIncome(
                            FakeData.fakeParticipantsSmall,
                            amount = 10.0
                        ),
                        FakeData.createFakePayment(
                            FakeData.fakeParticipantsSmall,
                            amount = 10.0
                        ),
                        FakeData.createFakeIncome(
                            FakeData.fakeParticipantsSmall,
                            amount = 10.0
                        ),
                        FakeData.createFakeIncome(
                            FakeData.fakeParticipantsSmall,
                            amount = 10.3
                        ),
                    )
                )
            )
            dataRepository.addGroup(
                Group(
                    currency = Currency.EURO, name = "Spanien",
                    participants = FakeData.fakeParticipantsSmall,
                    transactions = listOf(
                        FakeData.createFakeExpense(FakeData.fakeParticipantsSmall, amount = 100.0),
                        FakeData.createFakeExpense(FakeData.fakeParticipantsSmall, amount = 100.0),
                        FakeData.createFakeExpense(FakeData.fakeParticipantsSmall, amount = 100.0)
                    )
                )
            )
        }
    }

    private fun Group.formattedEventCosts(): String {
        return localeAwareFormatter.formatMoneyAmount(eventCost.execute(transactions), currency)
    }
}