package com.udacity.maluleque.meutako.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.udacity.maluleque.meutako.model.Transaction
import com.udacity.maluleque.meutako.repository.TransactionRepository
import com.udacity.maluleque.meutako.utils.Resource

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private var transactionRepository: TransactionRepository = TransactionRepository()

    fun getTransactions(userUid: String, initialDate: Long, endDate: Long, orderDirection: Query.Direction): LiveData<Resource<List<Transaction>>> {
        val transactions: MutableLiveData<Resource<List<Transaction>>> = MutableLiveData()
        transactionRepository.getUserTransactionsByDatesAndOrder(userUid, initialDate, endDate, orderDirection).observeForever {
            transactions.postValue(it)
        }
        return transactions
    }

}