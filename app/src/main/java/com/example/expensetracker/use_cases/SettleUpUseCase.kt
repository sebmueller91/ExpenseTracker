//package com.example.expensetracker.use_cases
//
//import android.util.Log
//import com.example.expensetracker.model.Event
//import com.example.expensetracker.model.MoneyAmout
//import com.example.expensetracker.model.Participant
//import com.example.expensetracker.model.Transaction
//import java.security.InvalidAlgorithmParameterException
//import java.util.Date
//
//
//class SettleUpUseCase {
//    class CalculateEventCost(
//        val event: Event
//    ) {
//        fun execute(): List<Transaction.Payment> {
////            val transactions = mutableListOf<Transaction.Payment>()
////            val balances = calculateParticipantBalances()
////
////            while (balances.any { it.amount != 0.0 }) {
////                val minIndex = balances.withIndex().minByOrNull { it.value.amount }
////                val maxIndex = balances.withIndex().minByOrNull { it.value.amount }
////                if (minIndex == null || maxIndex == null) {
////                    Log.e(SettleUpUseCase::class.java.simpleName, "minIndex or maxIndex is null!")
////                    throw InvalidAlgorithmParameterException()
////                }
////
////                transactions.add(
////                    Transaction.Payment(
////                        fromParticipant = event.participants[minIndex.index],
////                        toParticipant = event.participants[maxIndex.index],
////                        currency = event.currency,
////                        date = Date(),
////                        moneyAmout = balances[minIndex.index],
////                        purpose = "",
////                    )
////                )
////            }
//
//            return listOf()
//        }
//
//        private fun calculateParticipantBalances(): Array<MoneyAmout> {
//            val participantsBalances =
//                Array(event.participants.size) { MoneyAmout(0.0, event.currency) }
//
//            for (transaction in event.transactions) {
//                when (transaction) {
//                    is Transaction.Payment -> {
//                        participantsBalances.addToBalance(
//                            transaction.fromParticipant,
//                            transaction.moneyAmout
//                        )
//                        participantsBalances.subtractFromBalance(
//                            transaction.toParticipant,
//                            transaction.moneyAmout
//                        )
//                    }
//
//                    is Transaction.Expense -> {
//                        participantsBalances.addToBalance(
//                            transaction.paidBy,
//                            transaction.moneyAmout
//                        )
//                        transaction.splitBetween.forEach { participant ->
//                            participantsBalances.subtractFromBalance(
//                                participant,
//                                transaction.moneyAmout / transaction.splitBetween.size.toDouble()
//                            )
//                        }
//                    }
//
//                    is Transaction.Income -> {
//                        participantsBalances.subtractFromBalance(
//                            transaction.receivedBy,
//                            transaction.moneyAmout
//                        )
//                        transaction.splitBetween.forEach { participant ->
//                            participantsBalances.addToBalance(
//                                participant,
//                                transaction.moneyAmout / transaction.splitBetween.size.toDouble()
//                            )
//                        }
//                    }
//                }
//            }
//
//            return participantsBalances
//        }
//
//        private fun Array<MoneyAmout>.addToBalance(participant: Participant, amount: MoneyAmout) {
//            this[event.participants.indexOf(participant)] += amount
//        }
//
//        private fun Array<MoneyAmout>.subtractFromBalance(
//            participant: Participant,
//            amount: MoneyAmout
//        ) {
//            this[event.participants.indexOf(participant)] += amount
//        }
//    }
//}