package com.udacity.maluleque.meutako

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.udacity.maluleque.meutako.di.AppComponent
import com.udacity.maluleque.meutako.di.DaggerAppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent: AppComponent by lazy {
        // Creates an instance of AppComponent using its Factory constructor
        DaggerAppComponent.builder().build()
    }

}