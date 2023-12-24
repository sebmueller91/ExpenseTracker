package com.example.expensetracker.use_cases

import com.example.expensetracker.model.Transaction

class CalculateEventCost(
    val transactions: List<Transaction>
) {
    fun execute(): Double {
        return 0.0
    }
}

//class CalculateEventCost(
//    val group: Group
//) {
//    fun execute(): MoneyAmout {
//        var eventCosts = MoneyAmout(amount = 0.0, currency = group.currency)
//        for (transaction in group.transactions) {
//            when (transaction) {
//                is Transaction.Payment -> {}
//                is Transaction.Expense -> {
//                    eventCosts += transaction.moneyAmout
//                }
//
//                is Transaction.Income -> {
//                    eventCosts -= transaction.moneyAmout
//                }
//            }
//        }
//        return eventCosts
//    }
//}