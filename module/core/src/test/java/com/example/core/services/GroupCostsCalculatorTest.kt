package com.example.core.services

import com.example.core.util.FakeData
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

private val DELTA = 0.001

class GroupCostsCalculatorTest: KoinTest {
    private val useCasesTestModule = module {
        single<GroupCostsCalculator> { GroupCostsCalculatorImpl() }
    }

    private val sut: GroupCostsCalculator by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(useCasesTestModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun singleExpense() {
        val expense = FakeData.createFakeExpense(amount = 123.03)

        val result = sut.execute(listOf(expense))

        assertEquals(expense.amount, result, DELTA)
    }

    @Test
    fun singleIncome() {
        val income = FakeData.createFakeIncome(amount = 1243.03)

        val result = sut.execute(listOf(income))

        assertEquals(-income.amount, result, DELTA)
    }

    @Test
    fun singlePayment() {
        val payment = FakeData.createFakePayment(amount = 123.03)

        val result = sut.execute(listOf(payment))

        assertEquals(0.0, result, DELTA)
    }

    @Test
    fun multipleExpensesPaymentsIncomes() {
        val expenses = listOf(
            FakeData.createFakeExpense(amount = 10.0),
            FakeData.createFakeExpense(amount = 20.0),
            FakeData.createFakeExpense(amount = 25.0),
            FakeData.createFakeIncome(amount = 22.0),
            FakeData.createFakePayment(amount = 1000.0)
        )

        val result = sut.execute(expenses)

        assertEquals(33.0, result, DELTA)
    }
}