package com.example.core.services

import com.example.core.model.Currency
import com.example.core.model.Group
import com.example.core.model.ParticipantAmount
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

class IndividualPaymentPercentageCalculatorTest : KoinTest {
    private val groupCostMock = mockk<GroupCostsCalculator>()
    private val individualPaymentAmountCalculatorMock = mockk<IndividualPaymentAmountCalculator>()

    private val useCasesTestModule = module {
        single<IndividualPaymentPercentageCalculator> {
            IndividualPaymentPercentageCalculatorImpl(
                groupCostMock,
                individualPaymentAmountCalculatorMock
            )
        }
    }

    private val sut: IndividualPaymentPercentageCalculator by inject()

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
        every { groupCostMock.execute(group.transactions) } returns 100.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(
                participant,
                100.0
            )
        )

        val result = sut.execute(group)

        assertEquals(1, result.size)
        assertEquals(participant, result.first().participant)
        assertEquals(100.0, result.first().percentage, DELTA)
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
        every { groupCostMock.execute(any()) } returns 240.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(participant1, 120.0),
            ParticipantAmount(participant2, 120.0),
        )

        val result = sut.execute(group)

        assertEquals(2, result.size)
        assertEquals(50.0, result.first { it.participant == participant1}.percentage, DELTA)
        assertEquals(50.0, result.first { it.participant == participant2 }.percentage, DELTA)
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
        every { groupCostMock.execute(any()) } returns 200.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(participant1, 100.0),
            ParticipantAmount(participant2, 100.0)
        )

        val result = sut.execute(group)

        assertEquals(2, result.size)
        assertEquals(50.0, result.first { it.participant == participant1}.percentage, DELTA)
        assertEquals(50.0, result.first { it.participant == participant2 }.percentage, DELTA)
    }

    @Test
    fun twoParticipantsIncludingPayment() {
        every { groupCostMock.execute(any()) } returns 100.0

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
        every { groupCostMock.execute(any()) } returns 200.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(participant1, 120.0),
            ParticipantAmount(participant2, 80.0)
        )

        val result = sut.execute(group)

        assertEquals(2, result.size)
        assertEquals(60.0, result.first { it.participant == participant1}.percentage, DELTA)
        assertEquals(40.0, result.first { it.participant == participant2 }.percentage, DELTA)
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
        every { groupCostMock.execute(any()) } returns 180.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(participant1, 100.0),
            ParticipantAmount(participant2, 80.0)
        )

        val result = sut.execute(group)

        assertEquals(2, result.size)
        assertEquals(100.0 / 1.8, result.first { it.participant == participant1}.percentage, DELTA)
        assertEquals(80.0 / 1.8, result.first { it.participant == participant2 }.percentage, DELTA)
    }

    @Test
    fun twoParticipantsIncludingIncomeForOneParticipant() {
        every { groupCostMock.execute(any()) } returns 180.0

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
        every { groupCostMock.execute(any()) } returns 180.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(participant1, 100.0),
            ParticipantAmount(participant2, 80.0)
        )

        val result = sut.execute(group)

        assertEquals(2, result.size)
        assertEquals(100.0 / 1.8, result.first { it.participant == participant1}.percentage, DELTA)
        assertEquals(80.0 / 1.8, result.first { it.participant == participant2 }.percentage, DELTA)
    }

    @Test
    fun twoParticipantsWithOneParticipantNegative() {
        every { groupCostMock.execute(any()) } returns 180.0

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
        every { groupCostMock.execute(any()) } returns 80.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(participant1, 100.0),
            ParticipantAmount(participant2, -20.0)
        )

        val result = sut.execute(group)

        assertEquals(2, result.size)
        assertEquals(100.0, result.first { it.participant == participant1}.percentage, DELTA)
        assertEquals(0.0, result.first { it.participant == participant2 }.percentage, DELTA)
    }

    @Test
    fun threeParticipantsWithNormalization() {
        every { groupCostMock.execute(any()) } returns 180.0

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
        every { groupCostMock.execute(any()) } returns 280.0
        every { individualPaymentAmountCalculatorMock.execute(group) } returns listOf(
            ParticipantAmount(participant1, 100.0),
            ParticipantAmount(participant2, -20.0),
            ParticipantAmount(participant3, 200.0)
        )

        val result = sut.execute(group)

        assertEquals(3, result.size)
        assertEquals(100.0 / 3.0, result.first { it.participant == participant1 }.percentage, DELTA)
        assertEquals(0.0, result.first { it.participant == participant2 }.percentage, DELTA)
        assertEquals(2.0 * (100 / 3.0), result.first { it.participant == participant3 }.percentage, DELTA)
    }
}