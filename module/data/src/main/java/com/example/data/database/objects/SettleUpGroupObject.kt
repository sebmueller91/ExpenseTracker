package com.example.data.database.objects

import com.example.core.model.SettleUpGroup
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

internal class SettleUpGroupObject : RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var group: GroupObject? = GroupObject()
    var settleUpTransactions: RealmList<TransferObject> = realmListOf()
    var eventCosts: Double = 0.0
}

internal fun SettleUpGroupObject.toSettleUpGroup(): SettleUpGroup = SettleUpGroup(
    group = this@toSettleUpGroup.group?.toGroup()!!,
    settleUpTransactions = this@toSettleUpGroup.settleUpTransactions.map { it.toTransfer() },
    eventCosts = eventCosts
)