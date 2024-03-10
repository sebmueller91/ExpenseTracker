package com.example.data.database.objects

import com.example.core.model.Currency
import com.example.core.model.Group
import com.example.core.model.Transaction
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.UUID

internal class GroupObject : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var id: UUID = UUID.randomUUID()
    var name: String = ""
    var participants: List<ParticipantObject> = listOf()
    var currency: CurrencyObject = Currency.EURO.toCurrencyObject()
    var transfers: List<TransferObject> = listOf()
    var expenses: List<ExpenseObject> = listOf()
    var incomes: List<IncomeObject> = listOf()
}

internal fun GroupObject.toGroup(): Group {
    val transactions: List<Transaction> =
        this@toGroup.transfers.map { it.toTransfer() } + this@toGroup.expenses.map { it.toExpense() } + this@toGroup.incomes.map { it.toIncome() }
    return Group(
        id = this@toGroup.id,
        name = this@toGroup.name,
        participants = this@toGroup.participants.map { it.toParticipant() },
        currency = this@toGroup.currency.toCurrency(),
        transactions = transactions
    )
}

internal fun Group.toGroupObject(): GroupObject = toGroupObject().apply {
    id = this@toGroupObject.id
    name = this@toGroupObject.name
    participants = this@toGroupObject.participants.map { it.toParticipantObject() }
    currency = this@toGroupObject.currency.toCurrencyObject()
    transfers = this@toGroupObject.transactions.filterIsInstance<Transaction.Transfer>().map { it.toTransferObject()}
    expenses = this@toGroupObject.transactions.filterIsInstance<Transaction.Expense>().map { it.toExpenseObject()}
    incomes = this@toGroupObject.transactions.filterIsInstance<Transaction.Income>().map { it.toIncomeObject()}
}