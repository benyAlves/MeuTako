package com.bernardo.maluleque.shibaba

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bernardo.maluleque.shibaba.model.User
import com.bernardo.maluleque.shibaba.utils.Status
import com.bernardo.maluleque.shibaba.viewmodel.AuthViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SignInActivity : AppCompatActivity() {


    var providers: List<IdpConfig>? = null
    val authViewModel: AuthViewModel by viewModel()


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
                val authenticatedUser = FirebaseAuth.getInstance().currentUser
                val user = User(authenticatedUser!!.uid, authenticatedUser.displayName, authenticatedUser.phoneNumber)
                authViewModel.saveUser(user).observe(this, androidx.lifecycle.Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                            finish()
                        }
                        Status.LOADING -> {
                            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                        }
                        Status.ERROR -> {
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                Toast.makeText(this, R.string.sign_in_failed_text, Toast.LENGTH_SHORT).show()
                Log.e(TAG, String.format("%s with code %d", getString(R.string.sign_in_failed_text), resultCode))
                if (response!!.error != null) {
                    Log.e(TAG, getString(R.string.sign_in_failed_text), response.error)
                }
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