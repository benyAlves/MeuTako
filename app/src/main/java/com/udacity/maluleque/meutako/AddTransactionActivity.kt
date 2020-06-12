package com.udacity.maluleque.meutako

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.udacity.maluleque.meutako.model.Category
import com.udacity.maluleque.meutako.model.Transaction
import com.udacity.maluleque.meutako.utils.DateUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {
    @JvmField
    @BindView(R.id.textInputLayoutAmount)
    var textInputLayoutAmount: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutCategory)
    var textInputLayoutCategory: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutDescription)
    var textInputLayoutDescription: TextInputLayout? = null

    @JvmField
    @BindView(R.id.textInputLayoutDate)
    var textInputLayoutDate: TextInputLayout? = null

    @JvmField
    @BindView(R.id.dataInputText)
    var dataInputText: TextInputEditText? = null

    @JvmField
    @BindView(R.id.amountInputText)
    var amountInputText: TextInputEditText? = null

    @JvmField
    @BindView(R.id.categoryInputText)
    var categoryInputText: TextInputEditText? = null

    @JvmField
    @BindView(R.id.descriptionInputText)
    var descriptionInputText: TextInputEditText? = null

    @JvmField
    @BindView(R.id.imageView)
    var imageView: ImageView? = null

    @JvmField
    @BindView(R.id.radioGroup)
    var radioGroup: RadioGroup? = null
    private var transactionType: String? = null
    private var db: FirebaseFirestore? = null
    private var builder: AlertDialog.Builder? = null
    private var selectedCategory: String? = null
    private var date: Date? = null
    private val permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected: ArrayList<*> = ArrayList<Any?>()
    private val permissions: ArrayList<*> = ArrayList<Any?>()
    private var storage: FirebaseStorage? = null
    private val imageBitmap: Bitmap? = null
    private val currentPhotoPath: String? = null
    var photoFile: File? = null
    private val fileUri: Uri? = null
    private var user: FirebaseUser? = null
    private var transaction: Transaction? = null
    private var file: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        ButterKnife.bind(this)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(DetailsActivity.Companion.TRANSACTION)) {
                transaction = intent.getParcelableExtra(DetailsActivity.Companion.TRANSACTION)
                populateData(transaction)
            }
        }
        radioGroup!!.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            if (checkedId == R.id.radioButtonExpense) {
                transactionType = EXPENSE
            } else if (checkedId == R.id.radioButtonIncome) {
                transactionType = INCOME
            }
            categoryInputText!!.text!!.clear()
        }
        categoryInputText!!.setOnClickListener { v: View? ->
            if (transactionType != null) {
                getCategories(transactionType!!)
            } else {
                Toast.makeText(this, "Select the transaction type", Toast.LENGTH_SHORT).show()
            }
        }
        dataInputText!!.setOnClickListener { v: View? -> selectDate() }
        setDefaultDate()
    }

    private fun setDefaultDate() {
        date = Date()
        dataInputText!!.setText(DateUtils.formatDate(date))
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]
        val picker = DatePickerDialog(this,
                OnDateSetListener { view: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    calendar[year1, monthOfYear] = dayOfMonth
                    date = calendar.time
                    dataInputText!!.setText(DateUtils.formatDate(date))
                },
                year, month, day)
        picker.show()
    }

    private fun getCategories(transactionType: String) {
        db!!.collection("categories").whereEqualTo("type", transactionType)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot> ->
                    val categories = ArrayList<String>()
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val category = document.toObject(Category::class.java)
                            categories.add(category.name!!)
                        }
                        prepareCategoriesDialog(categories)
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun prepareCategoriesDialog(categories: ArrayList<String>) {
        val items = categories.toTypedArray()
        builder = AlertDialog.Builder(this)
        builder!!.setTitle(R.string.select_category)
                .setItems(items) { dialog: DialogInterface?, which: Int ->
                    categoryInputText!!.setText(categories[which])
                    selectedCategory = categories[which]
                }
        builder!!.create()
        builder!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_add_transaction, menu)
        return true
    }

    @OnClick(R.id.buttonAddImage)
    fun addImage() {
        captureImage()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveTransaction()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateData(transaction: Transaction?) {
        transactionType = transaction!!.type
        selectedCategory = transaction.category
        descriptionInputText!!.setText(transaction.description)
        amountInputText!!.setText(transaction.amount.toString())
        categoryInputText!!.setText(transaction.category)
        if (transactionType == INCOME) {
            radioGroup!!.check(R.id.radioButtonIncome)
        } else if (transactionType == EXPENSE) {
            radioGroup!!.check(R.id.radioButtonExpense)
        }
        if (transaction.image != null) {
            if (!transaction.image!!.trim { it <= ' ' }.isEmpty()) {
                storage!!.reference.child(transaction.image!!).downloadUrl.addOnSuccessListener { uri: Uri? ->
                    Picasso.get()
                            .load(uri)
                            .centerCrop()
                            .resize(500, 500)
                            .error(R.drawable.no_image)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView)
                }.addOnFailureListener { exception: Exception? -> Log.e(TAG, "Error downloading", exception) }
            }
        }
    }

    private fun saveTransaction() {
        var storageReference: StorageReference? = null
        if (file != null) {
            storageReference = storage!!.reference.child("images").child(user!!.uid + "/" + file!!.lastPathSegment)
            val uploadTask = storageReference.putFile(file!!)
            Log.d(TAG, "Path: " + storageReference.path)
            uploadTask.addOnFailureListener { exception ->
                Log.e(TAG, "Error uploading", exception)
                Toast.makeText(this@AddTransactionActivity, "Image not uploaded, try again", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot -> Log.d(TAG, "Uploaded " + taskSnapshot.metadata!!.path) }
        }
        if (isValidInput) {
            val document: DocumentReference
            if (transaction == null) {
                document = db!!.collection("users").document(user!!.uid).collection("transactions").document()
                transaction = Transaction()
            } else {
                document = db!!.collection("users").document(user!!.uid).collection("transactions").document(transaction!!.uid!!)
            }
            transaction!!.uid = document.id
            transaction!!.type = transactionType
            transaction!!.amount = amountInputText!!.text.toString().toDouble()
            transaction!!.category = selectedCategory
            transaction!!.date = date!!.time
            transaction!!.description = descriptionInputText!!.text.toString()
            if (storageReference != null) {
                if (storageReference.path != null) {
                    transaction!!.image = storageReference.path
                }
            }
            document.set(transaction!!)
                    .addOnSuccessListener { aVoid: Void? ->
                        Toast.makeText(this@AddTransactionActivity, "$transactionType added", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener { e: Exception? -> Log.e(TAG, "Error adding transaction", e) }
        }
    }

    private fun captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else if (outputMediaFile != null) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            file = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", outputMediaFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            startActivityForResult(intent, 100)
        } else {
            Toast.makeText(this, "Ups, Could not open camera now", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                Picasso.get()
                        .load(file)
                        .centerCrop()
                        .error(R.drawable.no_image)
                        .placeholder(R.drawable.placeholder)
                        .resize(500, 500)
                        .into(imageView)
            }
        }
    }

    private val isValidInput: Boolean
        private get() {
            if (transactionType == null) {
                Toast.makeText(this, "Select one transaction type", Toast.LENGTH_SHORT).show()
                return false
            } else {
                textInputLayoutCategory!!.isErrorEnabled = false
            }
            if (categoryInputText!!.text.toString().isEmpty()) {
                textInputLayoutCategory!!.error = "Category is required"
                return false
            } else {
                textInputLayoutCategory!!.isErrorEnabled = false
            }
            if (amountInputText!!.text.toString().isEmpty()) {
                textInputLayoutAmount!!.error = "Amount is required"
                return false
            } else {
                textInputLayoutAmount!!.isErrorEnabled = false
            }
            return true
        }

    companion object {
        private const val INCOME = "Income"
        private const val EXPENSE = "Expense"
        private const val TAG = "AddTransactionActivity"
        const val REQUEST_IMAGE_CAPTURE = 1
        private const val PERMISSIONS_RESULT = 107
        private val outputMediaFile: File?
            private get() {
                val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Transaction Info")
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return null
                    }
                }
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                return File(mediaStorageDir.path + File.separator +
                        "IMG_" + timeStamp + ".jpg")
            }
    }
}