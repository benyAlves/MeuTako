package com.udacity.maluleque.meutako

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.maluleque.meutako.model.User
import com.udacity.maluleque.meutako.utils.Status
import com.udacity.maluleque.meutako.viewmodel.AuthViewModel
import java.util.*
import javax.inject.Inject

class SignInActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var providers: List<IdpConfig>? = null
    lateinit var authViewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        (application as App).appComponent.inject(this)

        authViewModel = ViewModelProvider(this, viewModelFactory).get(AuthViewModel::class.java)

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