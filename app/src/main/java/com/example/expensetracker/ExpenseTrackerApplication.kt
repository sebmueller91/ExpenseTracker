package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.data.di.dataModule
import com.example.expensetracker.ui.di.uiModule
import com.example.expensetracker.use_cases.di.useCasesModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ExpenseTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ExpenseTrackerApplication)
            modules(dataModule, useCasesModule, uiModule)
        }
    }
}