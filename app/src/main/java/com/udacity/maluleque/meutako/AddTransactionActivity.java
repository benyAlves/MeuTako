package com.udacity.maluleque.meutako;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.udacity.maluleque.meutako.model.Category;
import com.udacity.maluleque.meutako.utils.DateUtils;

import java.io.ByteArrayOutputStream;
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


        categoryInputText.setOnClickListener(v -> {
            if (transactionType != null) {
                builder.show();
            } else {
                Toast.makeText(this, "Select the transaction type", Toast.LENGTH_SHORT).show();
            }
        });

        dataInputText.setOnClickListener(v -> {
            selectDate();
        });

        setDefaultDate();


        // permissions.add(CAMERA);


    }

    private ArrayList findPermissions(ArrayList permissions) {
        return null;
    }

    private void setDefaultDate() {
        dataInputText.setText(DateUtils.formatDate(new Date()));
    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    dataInputText.setText(DateUtils.formatDate(calendar.getTime()));
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
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
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

        // Save a file: path for use with ACTION_VIEW intents
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
        storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("images");
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
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }


    }
    }


 /*   public Intent openImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> intents = new ArrayList();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            intents.add(intent);
        }


        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            intents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = intents.get(intents.size() - 1);
        for (Intent intent : intents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        intents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));

        return chooserIntent;
    }

    void initRequestPermissions(){
        permissionsToRequest = findPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), PERMISSIONS_RESULT);
            }
        }
    }
}*/
