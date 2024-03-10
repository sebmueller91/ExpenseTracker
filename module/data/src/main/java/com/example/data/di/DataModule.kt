package com.example.data.di

import com.example.data.database.objects.GroupObject
import com.example.data.repository.DatabaseRepository
import com.example.data.repository.DatabaseRepositoryFakeImpl
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module

val dataModule = module {
    single<Realm> {
        Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    GroupObject::class,

                )
            )
        )
    }

    single<DatabaseRepository> { DatabaseRepositoryFakeImpl() }
}