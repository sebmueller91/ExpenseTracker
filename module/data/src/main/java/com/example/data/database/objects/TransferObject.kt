package com.example.data.database.objects

import com.example.data.model.Transaction
import io.realm.kotlin.types.RealmObject

internal class TransferObject : RealmObject{
}

internal fun TransferObject.toTransfer(): Transaction.Transfer {
    throw NotImplementedError()
}

internal fun Transaction.Transfer.toTransferObject(): TransferObject {
    throw NotImplementedError()
}