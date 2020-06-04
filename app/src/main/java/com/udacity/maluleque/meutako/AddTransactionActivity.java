package com.udacity.maluleque.meutako;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.udacity.maluleque.meutako.model.Category;
import com.udacity.maluleque.meutako.model.Transaction;
import com.udacity.maluleque.meutako.utils.DateUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.udacity.maluleque.meutako.DetailsActivity.TRANSACTION;

public class AddTransactionActivity extends AppCompatActivity {

    private static final String INCOME = "Income";
    private static final String EXPENSE = "Expense";
    private static final String TAG = "AddTransactionActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSIONS_RESULT = 107;

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
    private Date date;
    private ArrayList<String> permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private FirebaseStorage storage;
    private Bitmap imageBitmap;
    private String currentPhotoPath;
    File photoFile = null;
    private Uri fileUri;
    private FirebaseUser user;
    private Transaction transaction;
    private Uri file;

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Transaction Info");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(TRANSACTION)) {
                transaction = getIntent().getParcelableExtra(TRANSACTION);
                populateData(transaction);
            }
        }



        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonExpense) {
                transactionType = EXPENSE;
            } else if (checkedId == R.id.radioButtonIncome) {
                transactionType = INCOME;
            }
            categoryInputText.getText().clear();
        });


        categoryInputText.setOnClickListener(v -> {
            if (transactionType != null) {
                getCategories(transactionType);
            } else {
                Toast.makeText(this, "Select the transaction type", Toast.LENGTH_SHORT).show();
            }
        });

        dataInputText.setOnClickListener(v -> {
            selectDate();
        });

        setDefaultDate();

    }

    private void setDefaultDate() {
        date = new Date();
        dataInputText.setText(DateUtils.formatDate(date));
    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    date = calendar.getTime();
                    dataInputText.setText(DateUtils.formatDate(date));
                },
                year, month, day);
        picker.show();
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
                .setItems(items, (dialog, which) -> {
                    categoryInputText.setText(categories.get(which));
                    selectedCategory = categories.get(which);
                });
        builder.create();
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_transaction, menu);
        return true;
    }

    @OnClick(R.id.buttonAddImage)
    public void addImage() {
        captureImage();
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

    private void populateData(Transaction transaction) {
        transactionType = transaction.getType();
        selectedCategory = transaction.getCategory();
        descriptionInputText.setText(transaction.getDescription());
        amountInputText.setText(String.valueOf(transaction.getAmount()));
        categoryInputText.setText(transaction.getCategory());
        if (transactionType.equals(INCOME)) {
            radioGroup.check(R.id.radioButtonIncome);
        } else if (transactionType.equals(EXPENSE)) {
            radioGroup.check(R.id.radioButtonExpense);
        }
        if (transaction.getImage() != null) {
            if (!transaction.getImage().trim().isEmpty()) {
                storage.getReference().child(transaction.getImage()).getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get()
                            .load(uri)
                            .centerCrop()
                            .resize(500, 500)
                            .error(R.drawable.no_image)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Error downloading", exception);
                });
            }

        }
    }

    private void saveTransaction() {

        StorageReference storageReference = null;
        if (file != null) {
            storageReference = storage.getReference().child("images").child(user.getUid() + "/" + file.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(file);
            Log.d(TAG, "Path: " + storageReference.getPath());

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "Error uploading", exception);
                    Toast.makeText(AddTransactionActivity.this, "Image not uploaded, try again", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Uploaded " + taskSnapshot.getMetadata().getPath());
                }
            });
        }
        if (isValidInput()) {
            DocumentReference document;

            if (transaction == null) {
                document = db.collection("users").document(user.getUid()).collection("transactions").document();
                transaction = new Transaction();
            } else {
                document = db.collection("users").document(user.getUid()).collection("transactions").document(transaction.getUid());
            }

            transaction.setUid(document.getId());
            transaction.setType(transactionType);
            transaction.setAmount(Double.parseDouble(amountInputText.getText().toString()));
            transaction.setCategory(selectedCategory);
            transaction.setDate(date.getTime());
            transaction.setDescription(descriptionInputText.getText().toString());

            if (storageReference != null) {
                if (storageReference.getPath() != null) {
                    transaction.setImage(storageReference.getPath());
                }
            }

            document.set(transaction)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddTransactionActivity.this, transactionType + " added", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> Log.e(TAG, "Error adding transaction", e));


        }


    }

    private void captureImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else if (getOutputMediaFile() != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            file = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getOutputMediaFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(this, "Ups, Could not open camera now", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Picasso.get()
                        .load(file)
                        .centerCrop()
                        .error(R.drawable.no_image)
                        .placeholder(R.drawable.placeholder)
                        .resize(500, 500)
                        .into(imageView);
            }
        }
    }



    private boolean isValidInput() {

        if (transactionType == null) {
            Toast.makeText(this, "Select one transaction type", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            textInputLayoutCategory.setErrorEnabled(false);
        }

        if (categoryInputText.getText().toString().isEmpty()) {
            textInputLayoutCategory.setError("Category is required");
            return false;
        } else {
            textInputLayoutCategory.setErrorEnabled(false);
        }

        if (amountInputText.getText().toString().isEmpty()) {
            textInputLayoutAmount.setError("Amount is required");
            return false;
        } else {
            textInputLayoutAmount.setErrorEnabled(false);
        }
        return true;
    }

    }



