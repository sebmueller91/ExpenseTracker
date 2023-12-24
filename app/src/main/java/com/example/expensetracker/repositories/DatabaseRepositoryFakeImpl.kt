package com.example.expensetracker.repositories

import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.util.FakeData.Companion.createFakeExpense
import com.example.expensetracker.util.FakeData.Companion.createFakeIncome
import com.example.expensetracker.util.FakeData.Companion.createFakePayment
import com.example.expensetracker.util.FakeData.Companion.fakeParticipantsBig
import com.example.expensetracker.util.FakeData.Companion.fakeParticipantsSmall
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class DatabaseRepositoryFakeImpl : DatabaseRepository {
    private var _groups = MutableStateFlow(
        listOf(
            Group(
                currency = Currency.USD, name = "Summer Breeze",
                participants = (fakeParticipantsSmall),
                transactions = listOf(
                    createFakeExpense(fakeParticipantsSmall),
                    createFakeIncome(fakeParticipantsSmall),
                    createFakePayment(fakeParticipantsSmall),
                    createFakeExpense(fakeParticipantsSmall),
                    createFakeExpense(fakeParticipantsSmall),
                    createFakeExpense(fakeParticipantsSmall),
                    createFakeIncome(fakeParticipantsSmall),
                    createFakePayment(fakeParticipantsSmall),
                    createFakeIncome(fakeParticipantsSmall),
                    createFakeIncome(fakeParticipantsSmall),
                )
            ),
            Group(
                currency = Currency.EURO, name = "Rock im Park",
                participants = fakeParticipantsBig,
                transactions = listOf(
                    createFakeExpense(fakeParticipantsBig),
                    createFakeIncome(fakeParticipantsBig),
                    createFakeIncome(fakeParticipantsBig),
                    createFakePayment(fakeParticipantsBig),
                    createFakePayment(fakeParticipantsBig),
                    createFakeIncome(fakeParticipantsBig),
                    createFakeExpense(fakeParticipantsBig),
                    createFakeExpense(fakeParticipantsBig),
                    createFakeIncome(fakeParticipantsBig),
                    createFakeExpense(fakeParticipantsBig),
                    createFakeExpense(fakeParticipantsBig),
                    createFakeIncome(fakeParticipantsBig),
                    createFakePayment(fakeParticipantsBig),
                )
            )
        )
    )
    override val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    override fun addGroup(group: Group) {
        _groups.update {
            val newList = it.toMutableList()
            newList.add(group)
            newList.toList()
        }
    }

    override suspend fun getGroup(groupId: UUID): Group? {
        delay(1000)
        return _groups.value.firstOrNull { it.id == groupId }
    }
}