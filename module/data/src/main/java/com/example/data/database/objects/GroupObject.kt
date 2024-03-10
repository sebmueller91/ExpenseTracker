package com.example.data.database.objects

import com.example.data.model.Group
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

internal class GroupObject : RealmObject {
    @PrimaryKey val _id: ObjectId = ObjectId()
}

internal fun GroupObject.toGroup() : Group {
    throw NotImplementedError()
}

internal fun Group.toGroupObject() : GroupObject {
    throw NotImplementedError()
}