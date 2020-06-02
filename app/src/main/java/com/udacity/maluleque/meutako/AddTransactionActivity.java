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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.udacity.maluleque.meutako.model.Category;
import com.udacity.maluleque.meutako.model.Transaction;
import com.udacity.maluleque.meutako.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonExpense) {
                transactionType = EXPENSE;
            } else if (checkedId == R.id.radioButtonIncome) {
                transactionType = INCOME;
            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            fileUri = Uri.fromFile(photoFile);
            Picasso.get().load(fileUri).into(imageView);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
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
        /*storage = FirebaseStorage.getInstance();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        StorageReference imagesRef = storage.getReference().child("images/"+timeStamp+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (imageBitmap != null) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "Uploading Image: ", exception);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e(TAG, "Uploaded Image: " + taskSnapshot.getMetadata().getReference());
                }
            });
        }*/

        if (isValidInput()) {
            DocumentReference document = db.collection("users").document(user.getUid()).collection("transactions").document();
            Transaction transaction = new Transaction();
            transaction.setUid(document.getId());
            transaction.setType(transactionType);
            transaction.setAmount(Double.valueOf(amountInputText.getText().toString()));
            transaction.setCategory(selectedCategory);
            transaction.setDate(date.getTime());
            transaction.setDescription(descriptionInputText.getText().toString());

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
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    File photoFile = createImageFile();
                    Log.i(TAG, photoFile.getAbsolutePath());

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.udacity.maluleque.meutako.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Error getting Image: " + ex);
                }


            } else {
                Log.e(TAG, "Error resolving intent");
            }
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



