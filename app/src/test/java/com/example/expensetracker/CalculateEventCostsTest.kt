package com.example.expensetracker

import com.example.expensetracker.use_cases.CalculateEventCost
import com.example.expensetracker.util.FakeData
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CalculateEventCostsTest {
    private val DELTA = 0.001

    @Test
    fun empty_list() {
        val sut = CalculateEventCost(listOf())
        val result = sut.execute()
        assertEquals(0.0, result, DELTA)
    }

    @Test
    fun singleExpense() {
        val expense = FakeData.createFakeExpense(amount = 123.03)
        val sut = CalculateEventCost(listOf(expense))

        val result = sut.execute()

        assertEquals(expense.amount, result, DELTA)
    }

    @Test
    fun singleIncome() {
        val expense = FakeData.createFakeIncome(amount = 1243.03)
        val sut = CalculateEventCost(listOf(expense))

        val result = sut.execute()

        assertEquals(-expense.amount, result, DELTA)
    }

    @Test
    fun singlePayment() {
        val expense = FakeData.createFakePayment(amount = 123.03)
        val sut = CalculateEventCost(listOf(expense))

        val result = sut.execute()

        assertEquals(0.0, result, DELTA)
    }

    @Test
    fun multipleExpensesPaymentsIncoms() {
        val sut = CalculateEventCost(listOf(
            FakeData.createFakeExpense(amount = 10.0),
            FakeData.createFakeExpense(amount = 20.0),
            FakeData.createFakeExpense(amount = 25.0),
            FakeData.createFakeIncome(amount = 22.0),
            FakeData.createFakePayment(amount = 1000.0)
        ))

        val result = sut.execute()

        assertEquals(33.0, result, DELTA)
    }
}