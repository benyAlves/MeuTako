package com.udacity.maluleque.meutako.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/* Tells dagger this is a Module where it defines how to provide certain types of dependencies*/
@Module
class AppModule {

    @Singleton
    @Provides
    fun providesFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun providesFireStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun providesCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }
}