package com.example.core.util

import com.example.core.model.Participant
import com.example.core.model.Transaction
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class FakeData {

    companion object {
        val fakeParticipantsSmall = listOf(
            Participant("Dennis"),
            Participant("Johnny")
        )
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
            Participant("Eli"),
            Participant("Participant with very long name")
        )

        fun createFakeExpense(
            participants: List<Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0),
            paidBy: Participant = participants.random(),
            splitBetween: List<Participant> = participants
        ): Transaction.Expense {
            return Transaction.Expense(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                paidBy = paidBy,
                purpose = createFakePurpose(),
                splitBetween = splitBetween
            )
        }

        fun createFakePayment(
            participants: List<Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0),
            fromParticipant: Participant = participants.subList(0, participants.size / 2).random(),
            toParticipant: Participant = participants.subList(participants.size / 2, participants.size)
                .random()
        ): Transaction.Transfer {
            return Transaction.Transfer(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                fromParticipant = fromParticipant,
                purpose = createFakePurpose(),
                toParticipant = toParticipant
            )
        }

        fun createFakeIncome(
            participants: List<Participant> = fakeParticipantsSmall,
            amount: Double = Random.nextDouble(0.1, 1200.0),
            receivedBy: Participant = participants.random(),
            splitBetween: List<Participant> = participants
        ): Transaction.Income {
            return Transaction.Income(
                amount = amount,
                date = createFakeDate(year = 2022, day = 23, month = 8),
                receivedBy = receivedBy,
                purpose = createFakePurpose(),
                splitBetween = splitBetween
            )
        }

        fun createFakeParticipant(name: String = "P${Random.nextInt()}"): Participant {
            return Participant(name)
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