package com.example.data.database.objects

import com.example.core.model.Currency
import com.example.core.model.Group
import com.example.core.model.Transaction
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.UUID

internal class GroupObject : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var id: String = ""
    var name: String = ""
    var participants: RealmList<ParticipantObject> = realmListOf()
    var currency: CurrencyObject = Currency.EURO.toCurrencyObject()
    var transfers: RealmList<TransferObject> = realmListOf()
    var expenses: RealmList<ExpenseObject> = realmListOf()
    var incomes: RealmList<IncomeObject> = realmListOf()
}

internal fun GroupObject.toGroup(): Group {
    val transactions: List<Transaction> =
        this@toGroup.transfers.map { it.toTransfer() } + this@toGroup.expenses.map { it.toExpense() } + this@toGroup.incomes.map { it.toIncome() }
    return Group(
        id = UUID.fromString(this@toGroup.id),
        name = this@toGroup.name,
        participants = this@toGroup.participants.map { it.toParticipant() },
        currency = this@toGroup.currency.toCurrency(),
        transactions = transactions
    )
}

internal fun Group.toGroupObject(): GroupObject = toGroupObject().apply {
    id = this@toGroupObject.id.toString()
    name = this@toGroupObject.name
    participants = this@toGroupObject.participants.map { it.toParticipantObject() }.toRealmList()
    currency = this@toGroupObject.currency.toCurrencyObject()
    transfers = this@toGroupObject.transactions.filterIsInstance<Transaction.Transfer>()
        .map { it.toTransferObject() }.toRealmList()
    expenses = this@toGroupObject.transactions.filterIsInstance<Transaction.Expense>()
        .map { it.toExpenseObject() }.toRealmList()
    incomes = this@toGroupObject.transactions.filterIsInstance<Transaction.Income>()
        .map { it.toIncomeObject() }.toRealmList()
}