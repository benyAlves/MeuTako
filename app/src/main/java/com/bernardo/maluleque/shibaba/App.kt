package com.bernardo.maluleque.shibaba

import android.app.Application
import com.bernardo.maluleque.shibaba.di.appModule
import com.bernardo.maluleque.shibaba.di.viewModelModule
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(viewModelModule, appModule))
        }
    }

}