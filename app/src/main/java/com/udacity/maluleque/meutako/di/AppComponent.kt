package com.udacity.maluleque.meutako.di

import com.udacity.maluleque.meutako.AddTransactionActivity
import com.udacity.maluleque.meutako.SettingsActivity
import com.udacity.maluleque.meutako.SignInActivity
import com.udacity.maluleque.meutako.TransactionListFragment
import dagger.Component
import javax.inject.Singleton

/*
App component generates all dependencies required to satisfy the parameters of the
methods it expose. It generates the dependencies graph
* */

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(signInActivity: SignInActivity)
    fun inject(addTransactionActivity: AddTransactionActivity)
    fun inject(transactionListFragment: TransactionListFragment)
    fun inject(settingsFragment: SettingsActivity.SettingsFragment)

}