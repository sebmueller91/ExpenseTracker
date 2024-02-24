package com.example.expensetracker.data.realm.objects

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class GroupObject : RealmObject {
    @PrimaryKey val _id: ObjectId = ObjectId()
}

sealed class TransactionObject : RealmObject {

}