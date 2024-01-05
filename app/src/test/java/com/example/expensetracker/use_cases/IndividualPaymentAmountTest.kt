package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.util.FakeData
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class IndividualPaymentAmountTest : KoinTest {

    private val DELTA = 0.001

    private val useCasesTestModule = module {
        single<IndividualPaymentAmount> { IndividualPaymentAmountImpl() }
    }

    private val sut: IndividualPaymentAmount by inject()

    @Before
    fun setUp() {
        startKoin { modules(useCasesTestModule) }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun singleParticipant() {
        val participant = FakeData.createFakeParticipant()
        val expense = FakeData.createFakeExpense(participants = listOf(participant))
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant),
            transactions = listOf(expense)
        )

        val result = sut.execute(group)

        assertEquals(1, result.entries.size)
        assertEquals(participant, result.entries.first().key)
        assertEquals(expense.amount, result.entries.first().value, DELTA)
    }

    @Test
    fun twoParticipantsEqualShare() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2),
            transactions = listOf(
                FakeData.createFakeExpense(participants = listOf(participant1), amount = 100.0),
                FakeData.createFakeExpense(participants = listOf(participant2), amount = 100.0)
            )
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(100.0, result[participant1]!!, DELTA)
        assertEquals(100.0, result[participant2]!!, DELTA)
    }

    @Test
    fun twoParticipantsNonEqualShare() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2),
                    amount = 100.0,
                    paidBy = participant1
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant2),
                    amount = 80.0,
                    paidBy = participant2
                )
            )
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(100.0, result[participant1]!!, DELTA)
        assertEquals(80.0, result[participant2]!!, DELTA)
    }

    @Test
    fun twoParticipantsIncludingPayment() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1),
                    amount = 100.0,
                    paidBy = participant1
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant2),
                    amount = 100.0,
                    paidBy = participant2
                ),
                FakeData.createFakePayment(
                    fromParticipant = participant1,
                    toParticipant = participant2,
                    amount = 20.0
                ),
            )
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(120.0, result[participant1]!!, DELTA)
        assertEquals(80.0, result[participant2]!!, DELTA)
    }

    @Test
    fun twoParticipantsIncludingIncome() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2),
                    amount = 100.0,
                    paidBy = participant1
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2),
                    amount = 100.0,
                    paidBy = participant2
                ),
                FakeData.createFakeIncome(
                    receivedBy = participant2,
                    amount = 20.0,
                    splitBetween = listOf(participant1, participant2)
                ),
            )
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(100.0, result[participant1]!!, DELTA)
        assertEquals(80.0, result[participant2]!!, DELTA)
    }

    @Test
    fun twoParticipantsIncludingIncomeForOneParticipant() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2),
                    amount = 100.0,
                    paidBy = participant1
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2),
                    amount = 100.0,
                    paidBy = participant2
                ),
                FakeData.createFakeIncome(
                    receivedBy = participant2,
                    splitBetween = listOf(participant2),
                    amount = 20.0
                ),
            )
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(100.0, result[participant1]!!, DELTA)
        assertEquals(80.0, result[participant2]!!, DELTA)
    }
}