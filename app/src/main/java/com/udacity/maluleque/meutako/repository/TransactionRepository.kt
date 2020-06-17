package com.udacity.maluleque.meutako.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.udacity.maluleque.meutako.model.Transaction
import com.udacity.maluleque.meutako.utils.Resource
import java.util.*

class TransactionRepository() {

    private val TAG: String? = "TransactionRepository"


    fun getTransactions(db: FirebaseFirestore, userUid: String, initialDate: Long, endDate: Long): LiveData<Resource<List<Transaction>>> {

        var transactionsLiveData = MutableLiveData<Resource<List<Transaction>>>()
        val transactions: MutableList<Transaction> = ArrayList()

        val query = db.collection("users")
                .document(userUid)
                .collection("transactions")
                .whereGreaterThanOrEqualTo("date", initialDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING)

        query.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->

            if (e != null) {
                transactionsLiveData.value = Resource.error(e.message, transactions)
            }

            for (doc in queryDocumentSnapshots!!) {
                transactions.add(doc.toObject(Transaction::class.java))
            }
            transactionsLiveData.value = Resource.success(transactions)
        }

        return transactionsLiveData
    }

}