package com.example.core.services

import android.content.Context
import com.example.core.model.Currency
import com.example.core.model.Group
import com.example.core.model.Transaction
import com.example.core.util.FakeData
import com.example.core.util.isEqualTo
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
    private val individualCostsAmount: IndividualCostsAmount by inject()
    private val individualPaymentAmount: IndividualPaymentAmount by inject()

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
        assert(result.filter { it.fromParticipant == participant3 }.size == 2)
        assert(result.filter { it.amount.isEqualTo(33.33) }.size == 2)
        assert(result.filter { it.purpose == fakePurpose }.size == 2)
    }

    @Test
    fun threeParticipants_TwoPayments() {
        val participant1 = FakeData.createFakeParticipant()
        val participant2 = FakeData.createFakeParticipant()
        val participant3 = FakeData.createFakeParticipant()
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = listOf(participant1, participant2, participant3),
            transactions = listOf(
                FakeData.createFakePayment(
                    fromParticipant = participant1,
                    toParticipant = participant2,
                    amount = 10.0
                ),
                FakeData.createFakePayment(
                    fromParticipant = participant2,
                    toParticipant = participant3,
                    amount = 10.0
                )
            )
        )

        val result = sut.execute(group)

        assert(result.size == 1)
        assert(result[0].fromParticipant == participant3)
        assert(result[0].toParticipant == participant1)
        assert(result[0].amount == 10.0)
        assert(result[0].purpose == fakePurpose)
    }

    @Test
    fun fiveParticipants_DifferentBalances() {
        val participants = listOf(
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant()
        )
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = participants,
            transactions = listOf(
                FakeData.createFakePayment(
                    fromParticipant = participants[0],
                    toParticipant = participants[1],
                    amount = 10.0
                ),
                FakeData.createFakePayment(
                    fromParticipant = participants[1],
                    toParticipant = participants[2],
                    amount = 10.0
                ),
                FakeData.createFakeExpense(
                    participants = participants,
                    paidBy = participants[3],
                    amount = 123.43
                ),
                FakeData.createFakeExpense(
                    participants = participants,
                    paidBy = participants[4],
                    amount = 23443.23
                ),
                FakeData.createFakeIncome(
                    participants = participants,
                    splitBetween = participants,
                    amount = 1000.0
                )
            )
        )

        val result = sut.execute(group)

        assertParticipantsBalancesAreZero(group, result)
    }

    @Test
    fun tenParticipants_DifferentBalances() {
        val participants = listOf(
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant(),
            FakeData.createFakeParticipant()
        )
        val group = Group(
            currency = Currency.CAD,
            name = "testGroup",
            participants = participants,
            transactions = listOf(
                FakeData.createFakePayment(
                    fromParticipant = participants[0],
                    toParticipant = participants[1],
                    amount = 10.0
                ),
                FakeData.createFakePayment(
                    fromParticipant = participants[1],
                    toParticipant = participants[2],
                    amount = 10.0
                ),
                FakeData.createFakeExpense(
                    participants = participants,
                    paidBy = participants[3],
                    amount = 123.43
                ),
                FakeData.createFakeExpense(
                    participants = participants,
                    paidBy = participants[4],
                    amount = 23443.23
                ),
                FakeData.createFakeIncome(
                    participants = participants,
                    receivedBy = participants[5],
                    splitBetween = participants,
                    amount = 1000.0
                ),
                FakeData.createFakeIncome(
                    participants = participants,
                    receivedBy = participants[6],
                    splitBetween = participants,
                    amount = 2000.0
                ),
                FakeData.createFakeExpense(
                    participants = participants,
                    paidBy = participants[7],
                    amount = 123.43,
                    splitBetween = listOf(participants[7])
                ),
                FakeData.createFakeExpense(
                    participants = participants,
                    paidBy = participants[8],
                    amount = 1263.43,
                    splitBetween = participants
                ),
                FakeData.createFakeExpense(
                    participants = participants,
                    paidBy = participants[9],
                    amount = 0.11,
                    splitBetween = participants
                ),
            )
        )

        val result = sut.execute(group)

        assertParticipantsBalancesAreZero(group, result)
    }

    private fun assertParticipantsBalancesAreZero(
        group: Group,
        settleUpTransactions: List<Transaction.Transfer>
    ) {
        val settledUpGroup = group.copy(transactions = group.transactions + settleUpTransactions)
        val payments = individualPaymentAmount.execute(settledUpGroup)
        val costs = individualCostsAmount.execute(settledUpGroup)

        assert(settledUpGroup.participants.toSet() == payments.map { it.participant }.toSet())
        assert(payments.map { it.participant }.toSet() == costs.map { it.participant }.toSet())

        val balances =
            payments.map { it.participant }
                .associateWith { participant -> payments.first { it.participant == participant }.amount - costs.first { it.participant == participant }.amount }
                .map { it.value }

        assert(balances.all { it.isEqualTo(0.0) })
    }
}