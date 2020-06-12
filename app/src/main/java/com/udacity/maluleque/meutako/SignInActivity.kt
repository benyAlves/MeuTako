package com.udacity.maluleque.meutako

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.udacity.maluleque.meutako.model.User
import java.util.*

class SignInActivity : AppCompatActivity() {
    var providers: List<IdpConfig>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        providers = Arrays.asList(GoogleBuilder().build())
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Log.d(TAG, user!!.displayName)
                saveUserInDatabase(user)
            } else {
                Toast.makeText(this, R.string.sign_in_failed_text, Toast.LENGTH_SHORT).show()
                Log.e(TAG, String.format("%s with code %d", getString(R.string.sign_in_failed_text), resultCode))
                if (response!!.error != null) {
                    Log.e(TAG, getString(R.string.sign_in_failed_text), response.error)
                }
            }
        }
    }

    private fun saveUserInDatabase(firebaseUser: FirebaseUser?) {
        val db = FirebaseFirestore.getInstance()
        val user = User(firebaseUser!!.uid, firebaseUser.displayName, firebaseUser.phoneNumber)
        db.collection("users")
                .document(firebaseUser.uid)
                .set(user).addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignInActivity, getText(R.string.error_adding_data), Toast.LENGTH_SHORT).show()
                        Log.w(TAG, getString(R.string.error_adding_data), task.exception)
                    }
                }
    }

    fun signInUser(view: View?) {
        Log.d(TAG, "Clicked")
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers!!)
                        .build(),
                RC_SIGN_IN)
    }

    companion object {
        private const val RC_SIGN_IN = 123
        private const val TAG = "SignInActivity"
    }
}