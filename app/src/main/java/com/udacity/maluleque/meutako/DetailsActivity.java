package com.udacity.maluleque.meutako;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.udacity.maluleque.meutako.model.Transaction;
import com.udacity.maluleque.meutako.utils.NumberUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    public static final String TRANSACTION = "transaction";
    private static final String TAG = "DetailsActivity";
    private Transaction transaction;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @BindView(R.id.textViewAmount)
    TextView textViewAmount;
    @BindView(R.id.textViewDescription)
    TextView textViewDescription;
    @BindView(R.id.textViewTransactionType)
    TextView textViewType;
    @BindView(R.id.textViewTransationCategory)
    TextView textViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(TRANSACTION)) {
            transaction = getIntent().getParcelableExtra(TRANSACTION);
        }

        populateData(transaction);
    }

    private void populateData(Transaction transaction) {
        textViewAmount.setText(NumberUtils.getFormattedAmount(transaction.getAmount()));
        textViewCategory.setText(transaction.getCategory());
        textViewDescription.setText(transaction.getDescription());
        textViewType.setText(transaction.getType());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteTransaction();
                return true;
            case R.id.action_edit:
                editTransaction();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editTransaction() {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra(DetailsActivity.TRANSACTION, transaction);
        startActivity(intent);
    }

    private void deleteTransaction() {
        db.collection("users")
                .document(user.getUid())
                .collection("transactions")
                .document(transaction.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DetailsActivity.this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetailsActivity.this, "Deletion failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting transaction", e);
                });
    }
}
