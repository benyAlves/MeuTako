package com.bernardo.maluleque.shibaba.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bernardo.maluleque.shibaba.model.Transaction
import com.bernardo.maluleque.shibaba.repository.TransactionRepository
import com.bernardo.maluleque.shibaba.utils.Resource
import com.google.firebase.firestore.Query

class TransactionViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    fun getTransactions(userUid: String, initialDate: Long, endDate: Long, orderDirection: Query.Direction): LiveData<Resource<List<Transaction>>> {
        val transactions: MutableLiveData<Resource<List<Transaction>>> = MutableLiveData()
        transactionRepository.getUserTransactionsByDatesAndOrder(userUid, initialDate, endDate, orderDirection).observeForever {
            transactions.postValue(it)
        }
        return transactions
    }

}