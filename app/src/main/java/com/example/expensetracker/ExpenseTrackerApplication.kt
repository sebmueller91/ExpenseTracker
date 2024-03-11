package com.example.expensetracker

import android.app.Application
import com.example.data.di.dataModule
import com.example.core.services.di.servicesModule
import com.example.expensetracker.ui.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class ExpenseTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@ExpenseTrackerApplication)
            modules(dataModule, servicesModule, uiModule)
        }
    }
}