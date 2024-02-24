package com.example.expensetracker.data.di

import com.example.expensetracker.data.realm.objects.GroupObject
import com.example.expensetracker.data.repository.DatabaseRepository
import com.example.expensetracker.data.repository.DatabaseRepositoryFakeImpl
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module

val databaseModule = module {
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