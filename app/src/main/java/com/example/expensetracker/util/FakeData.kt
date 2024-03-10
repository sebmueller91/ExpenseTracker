package com.example.expensetracker.util

import com.example.data.model.Participant
import com.example.data.model.Transaction
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class FakeData {

    companion object {
        val fakeParticipantsSmall = listOf(
            com.example.data.model.Participant("Dennis"),
            com.example.data.model.Participant("Johnny")
        )
        val fakeParticipantsBig = listOf(
            com.example.data.model.Participant("Dennis"),
            com.example.data.model.Participant("Johnny"),
            com.example.data.model.Participant("Alisa"),
            com.example.data.model.Participant("Sebastian"),
            com.example.data.model.Participant("Peter"),
            com.example.data.model.Participant("Jonas"),
            com.example.data.model.Participant("Lisa"),
            com.example.data.model.Participant("BLisa"),
            com.example.data.model.Participant("Alex"),
            com.example.data.model.Participant("Eli"),
            com.example.data.model.Participant("Participant with very long name")
        )

        fun createFakeExpense(
            participants: List<com.example.data.model.Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0),
            paidBy: com.example.data.model.Participant = participants.random(),
            splitBetween: List<com.example.data.model.Participant> = participants
        ): com.example.data.model.Transaction.Expense {
            return com.example.data.model.Transaction.Expense(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                paidBy = paidBy,
                purpose = createFakePurpose(),
                splitBetween = splitBetween
            )
        }

        fun createFakePayment(
            participants: List<com.example.data.model.Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0),
            fromParticipant: com.example.data.model.Participant = participants.subList(0, participants.size / 2).random(),
            toParticipant: com.example.data.model.Participant = participants.subList(participants.size / 2, participants.size)
                .random()
        ): com.example.data.model.Transaction.Transfer {
            return com.example.data.model.Transaction.Transfer(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                fromParticipant = fromParticipant,
                purpose = createFakePurpose(),
                toParticipant = toParticipant
            )
        }

        fun createFakeIncome(
            participants: List<com.example.data.model.Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0),
            receivedBy: com.example.data.model.Participant = participants.random(),
            splitBetween: List<com.example.data.model.Participant> = participants
        ): com.example.data.model.Transaction.Income {
            return com.example.data.model.Transaction.Income(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                receivedBy = receivedBy,
                purpose = createFakePurpose(),
                splitBetween = splitBetween
            )
        }

        fun createFakeParticipant(name: String = "P${Random.nextInt()}"): com.example.data.model.Participant {
            return com.example.data.model.Participant(name)
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