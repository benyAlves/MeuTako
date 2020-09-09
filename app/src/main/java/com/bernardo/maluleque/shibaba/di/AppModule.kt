package com.bernardo.maluleque.shibaba.di

import com.bernardo.maluleque.shibaba.database.ShibabaDatabase
import com.bernardo.maluleque.shibaba.database.dao.CategoryDao
import com.bernardo.maluleque.shibaba.database.dao.TransactionDao
import com.bernardo.maluleque.shibaba.repository.AuthRepository
import com.bernardo.maluleque.shibaba.repository.CategoryRepository
import com.bernardo.maluleque.shibaba.repository.TransactionRepository
import com.bernardo.maluleque.shibaba.viewmodel.AuthViewModel
import com.bernardo.maluleque.shibaba.viewmodel.CategoryViewModel
import com.bernardo.maluleque.shibaba.viewmodel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/*  of dependencies*/
val appModule = module {

    single { FirebaseFirestore.getInstance() as FirebaseFirestore }

    single { FirebaseStorage.getInstance() as FirebaseStorage }

    single { FirebaseAuth.getInstance().currentUser!! as FirebaseUser }

    single { AuthRepository(get()) }

    single { get<ShibabaDatabase>().categoryDao() as CategoryDao }

    single { get<ShibabaDatabase>().transactionDao() as TransactionDao }

    single { CategoryRepository(get()) }

    single { TransactionRepository(get()) }
}

val viewModelModule = module {

    viewModel { AuthViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { TransactionViewModel(get()) }
}