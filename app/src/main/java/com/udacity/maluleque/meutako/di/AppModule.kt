package com.udacity.maluleque.meutako.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/* Tells dagger this is a Module where it defines how to provide certain types of dependencies*/
@Module
class AppModule {

    @Singleton
    @Provides
    fun providesFirestoreModule(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}