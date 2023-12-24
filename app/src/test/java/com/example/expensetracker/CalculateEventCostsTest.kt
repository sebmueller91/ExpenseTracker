package com.example.expensetracker

import com.example.expensetracker.model.Transaction
import com.example.expensetracker.use_cases.CalculateEventCost
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Date

class CalculateEventCostsTest {
    private val THRESHOLD = 0.001

    @Test
    fun empty_list() {
        val sut = CalculateEventCost(listOf())
        val result = sut.execute()
        assertEquals(result, 0.0, THRESHOLD)
    }

    @Test
    fun singleExpense() {
        val expense = Transaction.Expense(amount = 22.3, date = Date(), paidBy = )
    }
}