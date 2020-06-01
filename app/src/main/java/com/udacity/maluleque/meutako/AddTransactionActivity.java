package com.udacity.maluleque.meutako;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.udacity.maluleque.meutako.model.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTransactionActivity extends AppCompatActivity {

    private static final String INCOME = "Income";
    private static final String EXPENSE = "Expense";
    private static final String TAG = "AddTransactionActivity";
    @BindView(R.id.textInputLayoutAmount)
    TextInputLayout textInputLayoutAmount;
    @BindView(R.id.textInputLayoutCategory)
    TextInputLayout textInputLayoutCategory;
    @BindView(R.id.textInputLayoutDescription)
    TextInputLayout textInputLayoutDescription;
    @BindView(R.id.textInputLayoutDate)
    TextInputLayout textInputLayoutDate;
    @BindView(R.id.dataInputText)
    TextInputEditText dataInputText;
    @BindView(R.id.amountInputText)
    TextInputEditText amountInputText;
    @BindView(R.id.categoryInputText)
    TextInputEditText categoryInputText;
    @BindView(R.id.descriptionInputText)
    TextInputEditText descriptionInputText;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    private String transactionType;
    private FirebaseFirestore db;
    private AlertDialog.Builder builder;
    private String selectedCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonExpense) {
                transactionType = EXPENSE;
            } else if (checkedId == R.id.radioButtonIncome) {
                transactionType = INCOME;
            }
            getCategories(transactionType);
        });


        textInputLayoutCategory.setOnClickListener(v -> {
            if (transactionType != null) {
                builder.show();
            }
        });

    }

    private void getCategories(String transactionType) {
        db.collection("categories").whereEqualTo("type", transactionType)
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> categories = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            categories.add(category.getName());
                        }
                        prepareCategoriesDialog(categories);
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void prepareCategoriesDialog(ArrayList<String> categories) {

        final String[] items = categories.toArray(new String[categories.size()]);

        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_category)
                .setItems(items, (dialog, which) -> categoryInputText.setText(categories.get(which)));
        builder.create();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_transaction, menu);
        return true;
    }

    @OnClick(R.id.buttonAddImage)
    public void addImage() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveTransaction() {
        //TODO save transaction
    }


}
