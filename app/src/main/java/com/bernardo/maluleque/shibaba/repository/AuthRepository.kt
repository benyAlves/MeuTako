package com.bernardo.maluleque.shibaba.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bernardo.maluleque.shibaba.model.User
import com.bernardo.maluleque.shibaba.utils.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/*
@Inject tells dagger how to create AuthRepository
and now dagger knows that AuthRepository has FirebaseFirestore dependency
* */
class AuthRepository constructor(db: FirebaseFirestore) {

    private val usersRef = db.collection("users")

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