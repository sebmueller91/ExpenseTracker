package com.example.data.database.objects

import com.example.data.model.Transaction
import io.realm.kotlin.types.RealmObject

internal class ExpenseObject : RealmObject{
}

internal fun ExpenseObject.toExpense(): Transaction.Expense {
    throw NotImplementedError()
}

internal fun Transaction.Expense.toExpenseObject(): ExpenseObject {
    throw NotImplementedError()
}