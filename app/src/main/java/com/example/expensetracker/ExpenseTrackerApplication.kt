package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

// TODO: Remove this class?
class ExpenseTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ExpenseTrackerApplication)
            modules(appModule)
        }
    }
}