package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.util.FakeData
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class SettleUpTests : KoinTest {
    private val DELTA = 0.001

    private val individualPaymentAmountMock: IndividualPaymentAmount =
        mockk<IndividualPaymentAmount>()
    private val individualCostsAmountMock: IndividualCostsAmount = mockk<IndividualCostsAmount>()


    private val useCasesTestModule = module {
        single<SettleUp> { SettleUpImpl(individualPaymentAmountMock, individualCostsAmountMock) }
    }

    private val sut: SettleUp by inject()

    @Before
    fun setUp() {
        GlobalContext.startKoin {
            modules(useCasesTestModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun singleParticipant_noSettleUp() {
        val participant = FakeData.createFakeParticipant()
        val expense = FakeData.createFakeExpense(participants = listOf(participant))
        val group = Group(
            currency = Currency.EURO,
            name = "testGroup",
            participants = listOf(participant),
            transactions = listOf(expense)
        )
        every { individualPaymentAmountMock.execute(group) } returns mapOf(participant to expense.amount)
        every { individualCostsAmountMock.execute(group) } returns mapOf(participant to expense.amount)

        val result = sut.execute(group)

        assert(result.isEmpty())
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
        every { individualPaymentAmountMock.execute(group) } returns mapOf(
            participant1 to 100.0,
            participant2 to 100.0
        )
        every { individualCostsAmountMock.execute(group) } returns mapOf(
            participant1 to 100.0,
            participant2 to 100.0
        )

        val result = sut.execute(group)

        assert(result.isEmpty())
    }
}