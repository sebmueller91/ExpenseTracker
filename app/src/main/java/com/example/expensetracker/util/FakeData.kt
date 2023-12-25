package com.example.expensetracker.util

import com.example.expensetracker.model.Participant
import com.example.expensetracker.model.Transaction
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class FakeData {

    companion object {
        val fakeParticipantsSmall = listOf(Participant("Dennis"), Participant("Johnny"))
        val fakeParticipantsBig = listOf(
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

        fun createFakeExpense(
            participants: List<Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0)
        ): Transaction.Expense {
            return Transaction.Expense(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                paidBy = participants.random(),
                purpose = createFakePurpose(),
                splitBetween = participants
            )
        }

        fun createFakePayment(participants: List<Participant> = fakeParticipantsSmall, amount: Double = Random.nextDouble(0.1, 1200.0)): Transaction.Payment {
            return Transaction.Payment(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                fromParticipant = participants.subList(0, participants.size / 2).random(),
                purpose = createFakePurpose(),
                toParticipant = participants.subList(participants.size / 2, participants.size)
                    .random()
            )
        }

        fun createFakeIncome(
            participants: List<Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0)
        ): Transaction.Income {
            return Transaction.Income(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                receivedBy = participants.random(),
                purpose = createFakePurpose(),
                splitBetween = participants
            )
        }

        private fun createFakeDate(year: Int, month: Int, day: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            return calendar.time
        }

        private fun createFakePurpose(): String {
            return listOf(
                "Eis",
                "Wasser",
                "Flughafen Toilette",
                "Restaurant in der Innenstadt"
            ).random()
        }
    }
}