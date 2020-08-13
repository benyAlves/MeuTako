package com.udacity.maluleque.meutako.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.udacity.maluleque.meutako.model.Transaction
import com.udacity.maluleque.meutako.repository.TransactionRepository
import com.udacity.maluleque.meutako.utils.Resource
import javax.inject.Inject

class TransactionViewModel @Inject constructor(private val transactionRepository: TransactionRepository) : ViewModel() {

    fun getTransactions(userUid: String, initialDate: Long, endDate: Long, orderDirection: Query.Direction): LiveData<Resource<List<Transaction>>> {
        val transactions: MutableLiveData<Resource<List<Transaction>>> = MutableLiveData()
        transactionRepository.getUserTransactionsByDatesAndOrder(userUid, initialDate, endDate, orderDirection).observeForever {
            transactions.postValue(it)
        }
        return transactions
    }

}