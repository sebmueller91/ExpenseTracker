package com.example.core.services

import com.example.core.model.Currency
import com.example.core.model.Group
import com.example.core.util.FakeData
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
private val DELTA = 0.001
class IndividualPaymentPercentageTest : KoinTest {
    private val eventCostMock = mockk<EventCosts>()
    private val individualPaymentAmountMock = mockk<IndividualPaymentAmount>()

    private val useCasesTestModule = module {
        single<IndividualPaymentPercentage> {
            IndividualPaymentPercentageImpl(
                eventCostMock,
                individualPaymentAmountMock
            )
        }
    }

    private val sut: IndividualPaymentPercentage by inject()

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
        every { eventCostMock.execute(group.transactions) } returns 100.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(participant to 100.0)

        val result = sut.execute(group)

        assertEquals(1, result.entries.size)
        assertEquals(participant, result.entries.first().key)
        assertEquals(100.0, result.entries.first().value, DELTA)
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
                FakeData.createFakeExpense(participants = listOf(participant1), amount = 120.0),
                FakeData.createFakeExpense(participants = listOf(participant2), amount = 120.0)
            )
        )
        every { eventCostMock.execute(any()) } returns 240.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 120.0,
            participant2 to 120.0
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(50.0, result[participant1]!!, DELTA)
        assertEquals(50.0, result[participant2]!!, DELTA)
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
                    amount = 100.0,
                    paidBy = participant2
                )
            )
        )
        every { eventCostMock.execute(any()) } returns 200.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 100.0,
            participant2 to 100.0
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(50.0, result[participant1]!!, DELTA)
        assertEquals(50.0, result[participant2]!!, DELTA)
    }

    @Test
    fun twoParticipantsIncludingPayment() {
        every { eventCostMock.execute(any()) } returns 100.0

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
        every { eventCostMock.execute(any()) } returns 200.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 120.0,
            participant2 to 80.0
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(60.0, result[participant1]!!, DELTA)
        assertEquals(40.0, result[participant2]!!, DELTA)
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
        every { eventCostMock.execute(any()) } returns 180.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 100.0,
            participant2 to 80.0
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(100.0 / 1.8, result[participant1]!!, DELTA)
        assertEquals(80.0 / 1.8, result[participant2]!!, DELTA)
    }

    @Test
    fun twoParticipantsIncludingIncomeForOneParticipant() {
        every { eventCostMock.execute(any()) } returns 180.0

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
        every { eventCostMock.execute(any()) } returns 180.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 100.0,
            participant2 to 80.0
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(100.0 / 1.8, result[participant1]!!, DELTA)
        assertEquals(80.0 / 1.8, result[participant2]!!, DELTA)
    }

    @Test
    fun twoParticipantsWithOneParticipantNegative() {
        every { eventCostMock.execute(any()) } returns 180.0

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
                FakeData.createFakeIncome(
                    receivedBy = participant2,
                    splitBetween = listOf(participant2),
                    amount = 20.0
                ),
            )
        )
        every { eventCostMock.execute(any()) } returns 80.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 100.0,
            participant2 to -20.0
        )

        val result = sut.execute(group)

        assertEquals(2, result.entries.size)
        assertEquals(100.0, result[participant1]!!, DELTA)
        assertEquals(0.0, result[participant2]!!, DELTA)
    }

    @Test
    fun threeParticipantsWithNormalization() {
        every { eventCostMock.execute(any()) } returns 180.0

        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val participant3 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2, participant3),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2),
                    amount = 100.0,
                    paidBy = participant1
                ),
                FakeData.createFakeIncome(
                    receivedBy = participant2,
                    splitBetween = listOf(participant2),
                    amount = 20.0
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2, participant3),
                    amount = 200.0,
                    paidBy = participant3
                ),
            )
        )
        every { eventCostMock.execute(any()) } returns 280.0
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 100.0,
            participant2 to -20.0,
            participant3 to 200.0
        )

        val result = sut.execute(group)

        assertEquals(3, result.entries.size)
        assertEquals(100.0/3.0, result[participant1]!!, DELTA)
        assertEquals(0.0, result[participant2]!!, DELTA)
        assertEquals(2.0*(100/3.0), result[participant3]!!, DELTA)
    }
}