package com.udacity.maluleque.meutako.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.udacity.maluleque.meutako.model.User
import com.udacity.maluleque.meutako.utils.Resource


class AuthRepository {

    private val rootRef = FirebaseFirestore.getInstance()
    private val usersRef = rootRef.collection("users")

    fun saveAuthenticatedUser(authenticatedUser: User): MutableLiveData<Resource<User>> {

        val newUser: MutableLiveData<Resource<User>> = MutableLiveData()

        val userDocument: DocumentReference = usersRef.document(authenticatedUser.uid)
        userDocument.get().addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                if (!it.result!!.exists()) {
                    usersRef.document(authenticatedUser.uid)
                            .set(authenticatedUser)
                            .addOnCompleteListener(OnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d(TAG, "user registered")
                                    newUser.value = Resource.success(authenticatedUser)
                                }
                            }).addOnFailureListener {
                                newUser.value = Resource.error("Error occurred saving user", authenticatedUser)
                                Log.e(TAG, "Error occurred saving user", it.cause)
                            }
                } else {
                    Log.d(TAG, "user exists")
                    newUser.value = Resource.success(authenticatedUser)
                }
            } else {
                newUser.value = Resource.error("Error occurred verifying user", authenticatedUser)
                Log.e(TAG, "Error occurred verifying user", it.exception)
            }
        })
        return newUser
    }

    companion object {
        const val TAG: String = "AuthRepository"
    }
}