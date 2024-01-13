package com.example.expensetracker.use_cases

import android.content.Context
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.util.FakeData
import com.example.expensetracker.util.isEqualTo
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

class SettleUpIntegrationTests : KoinTest {
    private val contextMock: Context = mockk<Context>(relaxed = true)
    private val fakePurpose = "asdasdjansdjasndjnbgfdsb"

    private val useCasesTestModule = module {
        single<IndividualCostsAmount> { IndividualCostsAmountImpl() }
        single<IndividualPaymentAmount> { IndividualPaymentAmountImpl() }
        single<SettleUp> {
            SettleUpImpl(
                individualCostsAmount = get(),
                individualPaymentAmount = get(),
                context = contextMock
            )
        }
    }

    private val sut: SettleUp by inject()

    @Before
    fun setUp() {
        every { contextMock.getString(any()) } returns fakePurpose

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

        val result = sut.execute(group)

        assert(result.isEmpty())
    }

    @Test
    fun twoParticipants_SingleExpense() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2),
                    paidBy = participant1,
                    amount = 100.0
                ),
            )
        )

        val result = sut.execute(group)

        assert(result.size == 1)
        assert(result[0].toParticipant == participant1)
        assert(result[0].fromParticipant == participant2)
        assert(result[0].amount == 50.0)
        assert(result[0].purpose == fakePurpose)
    }

    @Test
    fun threeParticipants_CircularExpenses() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val participant3 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2, participant3),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2, participant3),
                    paidBy = participant1,
                    amount = 100.0
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2, participant3),
                    paidBy = participant2,
                    amount = 100.0
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2, participant3),
                    paidBy = participant3,
                    amount = 100.0
                ),
            )
        )

        val result = sut.execute(group)

        assert(result.isEmpty())
    }

    @Test
    fun threeParticipants_TwoExpenses() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val participant3 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2, participant3),
            transactions = listOf(
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2, participant3),
                    paidBy = participant1,
                    amount = 100.0
                ),
                FakeData.createFakeExpense(
                    participants = listOf(participant1, participant2, participant3),
                    paidBy = participant2,
                    amount = 100.0
                )
            )
        )

        val result = sut.execute(group)

        assert(result.size == 2)
        assert(result.filter { it.toParticipant == participant1 }.size == 1)
        assert(result.filter { it.toParticipant == participant2 }.size == 1)
        assert(result.filter { it.fromParticipant == participant3}.size == 2)
        assert(result.filter { it.amount.isEqualTo(33.33) }.size == 2)
        assert(result.filter { it.purpose == fakePurpose}.size == 2)
    }
}