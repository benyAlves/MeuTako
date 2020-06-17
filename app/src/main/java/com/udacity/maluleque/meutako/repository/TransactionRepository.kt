package com.udacity.maluleque.meutako.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.udacity.maluleque.meutako.model.Transaction

class TransactionRepository(val db: FirebaseFirestore, val userUid: String, val initialDate: Long, val endDate: Long) {

    private val TAG: String? = "TransactionRepository"
    private lateinit var registration: ListenerRegistration
    var transactions: LiveData<MutableList<Transaction>> = TODO()

    fun getTransactions(dataMonth: String?) {
        val query = db.collection("users")
                .document(userUid)
                .collection("transactions")
                .whereGreaterThanOrEqualTo("date", initialDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING)

        registration = query.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
            if (e != null) {
                Log.e(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            for (doc in queryDocumentSnapshots!!) {
                //transactions.value = doc.toObject(Transaction::class.java)
            }
            return@addSnapshotListener
        }
    }

}