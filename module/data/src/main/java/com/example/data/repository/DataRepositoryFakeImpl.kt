package com.example.data.repository

import com.example.core.model.Currency
import com.example.core.model.Group
import com.example.core.model.Transaction
import com.example.core.util.FakeData.Companion.createFakeExpense
import com.example.core.util.FakeData.Companion.createFakeIncome
import com.example.core.util.FakeData.Companion.createFakePayment
import com.example.core.util.FakeData.Companion.fakeParticipantsBig
import com.example.core.util.FakeData.Companion.fakeParticipantsSmall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

//internal class DataRepositoryFakeImpl : DataRepository {
//    private var _groups = MutableStateFlow(
//        listOf(
//            Group(
//                currency = Currency.USD, name = "Summer Breeze",
//                participants = (fakeParticipantsSmall),
//                transactions = listOf(
//                    createFakeExpense(fakeParticipantsSmall, amount = 100.0),
//                    createFakeIncome(fakeParticipantsSmall, amount = 10.0),
//                    createFakePayment(fakeParticipantsSmall, amount = 20.0),
//                    createFakeExpense(fakeParticipantsSmall, amount = 100.24),
//                    createFakeExpense(fakeParticipantsSmall, amount = 100.20),
//                    createFakeExpense(fakeParticipantsSmall, amount = 100.0),
//                    createFakeIncome(fakeParticipantsSmall, amount = 10.0),
//                    createFakePayment(fakeParticipantsSmall, amount = 10.0),
//                    createFakeIncome(fakeParticipantsSmall, amount = 10.0),
//                    createFakeIncome(fakeParticipantsSmall, amount = 10.3),
//                )
//            ),
//            Group(
//                currency = Currency.EURO, name = "Rock im Park",
//                participants = fakeParticipantsBig,
//                transactions = listOf(
//                    createFakeExpense(fakeParticipantsBig, amount = 100.0),
//                    createFakeExpense(fakeParticipantsBig, amount = 100.90),
//                    createFakeExpense(fakeParticipantsBig, amount = 100.10),
//                    createFakeExpense(fakeParticipantsBig, amount = 100.23),
//                    createFakeExpense(fakeParticipantsBig, amount = 100.0),
//                    createFakePayment(fakeParticipantsBig, amount = 100.0),
//                    createFakePayment(fakeParticipantsBig, amount = 100.0),
//                    createFakePayment(fakeParticipantsBig, amount = 100.0),
//                    createFakePayment(fakeParticipantsBig, amount = 100.0),
//                    createFakePayment(fakeParticipantsBig, amount = 22.0),
//                )
//            ),
//            Group(
//                currency = Currency.EURO, name = "Spanien",
//                participants = fakeParticipantsSmall,
//                transactions = listOf(
//                    createFakeExpense(fakeParticipantsSmall, amount = 100.0),
//                    createFakeExpense(fakeParticipantsSmall, amount = 100.0),
//                    createFakeExpense(fakeParticipantsSmall, amount = 100.0)
//                )
//            )
//        )
//    )
//    override val groups: StateFlow<List<Group>> = _groups.asStateFlow()
//
//    override suspend fun addGroup(group: Group) {
//        _groups.update {
//            val newList = it.toMutableList()
//            newList.add(group)
//            newList.toList()
//        }
//    }
//
//    override fun addTransaction(groupId: UUID, transaction: Transaction) {
//        val groupIndex = _groups.value.indexOfFirst { it.id == groupId }
//        val group = _groups.value[groupIndex]
//        val updatedGroup = group.copy(transactions = group.transactions + transaction)
//        val updatedGroups = _groups.value.toMutableList().apply {
//            this[groupIndex] = updatedGroup
//        }
//        _groups.value = updatedGroups
//    }
//}