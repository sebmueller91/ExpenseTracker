package com.example.expensetracker.repositories

import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class DatabaseRepositoryFakeImpl : DatabaseRepository {
    private val participantsGroup1 = listOf(Participant("Dennis"), Participant("Johnny"))
    private val participantsGroup2 = listOf(
        Participant("Dennis"),
        Participant("Johnny"),
        Participant("Alisa"),
        Participant("Sebastian"),
        Participant("Peter"),
        Participant("Jonas"),
        Participant("Lisa"),
        Participant("BLisa"),
        Participant("Alex"),
        Participant("Eli")
    )

    private var _groups = MutableStateFlow(
        listOf(
            Group(
                currency = Currency.USD, name = "Summer Breeze",
                participants = participantsGroup1,
                transactions = listOf(
                    createExpense(participantsGroup1),
                    createIncome(participantsGroup1),
                    createPayment(participantsGroup1),
                    createExpense(participantsGroup1),
                    createExpense(participantsGroup1),
                    createExpense(participantsGroup1),
                    createIncome(participantsGroup1),
                    createPayment(participantsGroup1),
                    createIncome(participantsGroup1),
                    createIncome(participantsGroup1),
                )
            ),
            Group(
                currency = Currency.EURO, name = "Rock im Park",
                participants = participantsGroup2,
                transactions = listOf(
                    createExpense(participantsGroup2),
                    createIncome(participantsGroup2),
                    createIncome(participantsGroup2),
                    createPayment(participantsGroup2),
                    createPayment(participantsGroup2),
                    createIncome(participantsGroup2),
                    createExpense(participantsGroup2),
                    createExpense(participantsGroup2),
                    createIncome(participantsGroup2),
                    createExpense(participantsGroup2),
                    createExpense(participantsGroup2),
                    createIncome(participantsGroup2),
                    createPayment(participantsGroup2),
                )
            )
        )
    )
    override val groups = _groups.asStateFlow()

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

    private fun createExpense(participants: List<Participant>): Transaction.Expense {
        return Transaction.Expense(
            amount = Random.nextDouble(0.1, 1200.0),
            date = createDate(year = 2022, day = 23, month = 8),
            paidBy = participants.random(),
            purpose = createPurpose(),
            splitBetween = participants)
    }

    private fun createPayment(participants: List<Participant>): Transaction.Payment {
        return Transaction.Payment(
            amount = Random.nextDouble(0.1, 1200.0),
            date = createDate(year = 2022, day = 23, month = 8),
            fromParticipant = participants.subList(0, participants.size / 2).random(),
            purpose = createPurpose(),
            toParticipant = participants.subList(participants.size / 2, participants.size).random()
        )
    }

    private fun createIncome(participants: List<Participant>): Transaction.Income {
        return Transaction.Income(
            amount = Random.nextDouble(0.1, 1200.0),
            date = createDate(year = 2022, day = 23, month = 8),
            receivedBy = participants.random(),
            purpose = createPurpose(),
            splitBetween = participants
        )
    }

    private fun createDate(year: Int, month: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return calendar.time
    }

    private fun createPurpose(): String {
        return listOf(
            "Eis",
            "Wasser",
            "Flughafen Toilette",
            "Restaurant in der Innenstadt"
        ).random()
    }
}