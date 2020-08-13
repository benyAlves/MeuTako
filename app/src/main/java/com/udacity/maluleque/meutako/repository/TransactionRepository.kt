package com.udacity.maluleque.meutako.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.udacity.maluleque.meutako.model.Transaction
import com.udacity.maluleque.meutako.utils.Constants
import com.udacity.maluleque.meutako.utils.Resource
import java.util.*
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val db: FirebaseFirestore) {


    fun getUserTransactionsByDatesAndOrder(userUid: String, initialDate: Long, endDate: Long, orderDirection: Query.Direction): LiveData<Resource<List<Transaction>>> {

        var transactionsLiveData = MutableLiveData<Resource<List<Transaction>>>()
        val transactions: MutableList<Transaction> = ArrayList()

        val query = db.collection(Constants.USERS)
                .document(userUid)
                .collection(Constants.TRANSACTIONS)
                .whereGreaterThanOrEqualTo("date", initialDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", orderDirection)

        query.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->

            if (e != null) {
                transactionsLiveData.value = Resource.error(e.message, transactions)
            }

            if (queryDocumentSnapshots != null) {

                for (doc in queryDocumentSnapshots) {
                    transactions.add(doc.toObject(Transaction::class.java))
                }
            }

            transactionsLiveData.value = Resource.success(transactions)
        }

        return transactionsLiveData
    }

    companion object {
        private const val TAG: String = "TransactionRepository"
    }

}