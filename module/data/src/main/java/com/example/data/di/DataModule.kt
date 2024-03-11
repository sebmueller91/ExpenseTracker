package com.example.data.di

import com.example.data.database.objects.CurrencyObject
import com.example.data.database.objects.ExpenseObject
import com.example.data.database.objects.GroupObject
import com.example.data.database.objects.IncomeObject
import com.example.data.database.objects.ParticipantObject
import com.example.data.database.objects.TransferObject
import com.example.data.repository.DataRepository
import com.example.data.repository.DataRepositoryImpl
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.dsl.module

val dataModule = module {
    single<Realm> {
        Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    CurrencyObject::class,
                    ExpenseObject::class,
                    IncomeObject::class,
                    TransferObject::class,
                    ParticipantObject::class,
                    GroupObject::class,
                )
            )
        )
    }

    single<DataRepository> { DataRepositoryImpl(realmDb = get()) }
}