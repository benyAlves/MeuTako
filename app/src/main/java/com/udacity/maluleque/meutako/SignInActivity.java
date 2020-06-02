package com.udacity.maluleque.meutako;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.udacity.maluleque.meutako.model.User;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "SignInActivity";
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, user.getDisplayName());
                saveUserInDatabase(user);

            } else {
                Toast.makeText(this, R.string.sign_in_failed_text, Toast.LENGTH_SHORT).show();
                Log.e(TAG, String.format("%s with code %d", getString(R.string.sign_in_failed_text), resultCode));
                if (response.getError() != null) {
                    Log.e(TAG, getString(R.string.sign_in_failed_text), response.getError());
                }
            }
        }
    }

    private void saveUserInDatabase(FirebaseUser firebaseUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getPhoneNumber());
        db.collection("users")
                .document(firebaseUser.getUid())
                .set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(SignInActivity.this, getText(R.string.error_adding_data), Toast.LENGTH_SHORT).show();
                Log.w(TAG, getString(R.string.error_adding_data), task.getException());
            }
        });

    }

    public void signInUser(View view) {
        Log.d(TAG, "Clicked");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
}
