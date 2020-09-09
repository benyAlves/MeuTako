package com.bernardo.maluleque.shibaba

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.bernardo.maluleque.shibaba.model.Transaction
import com.bernardo.maluleque.shibaba.utils.NumberUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class DetailsActivity : AppCompatActivity() {
    private var transaction: Transaction? = null
    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null

    @JvmField
    @BindView(R.id.textViewAmount)
    var textViewAmount: TextView? = null

    @JvmField
    @BindView(R.id.textViewDescription)
    var textViewDescription: TextView? = null

    @JvmField
    @BindView(R.id.textViewTransactionType)
    var textViewType: TextView? = null

    @JvmField
    @BindView(R.id.textViewTransationCategory)
    var textViewCategory: TextView? = null

    @JvmField
    @BindView(R.id.imageView)
    var imageView: ImageView? = null

    @JvmField
    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null
    private var storage: FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        ButterKnife.bind(this)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        storage = FirebaseStorage.getInstance()
        val extras = intent.extras
        if (extras!!.containsKey(TRANSACTION)) {
            transaction = intent.getParcelableExtra(TRANSACTION)
        }
        populateData(transaction)
    }

    private fun populateData(transaction: Transaction?) {
        textViewAmount!!.text = NumberUtils.getFormattedAmount(transaction!!.amount)
        textViewCategory!!.text = transaction.category
        textViewDescription!!.text = transaction.description
        textViewType!!.text = transaction.type
        if (transaction.image != null) {
            if (!transaction.image!!.trim { it <= ' ' }.isEmpty()) {
                progressBar!!.visibility = View.VISIBLE
                storage!!.reference.child(transaction.image!!).downloadUrl.addOnSuccessListener { uri: Uri? ->
                    progressBar!!.visibility = View.INVISIBLE
                    Picasso.get()
                            .load(uri)
                            .centerCrop()
                            .error(R.drawable.no_image)
                            .placeholder(R.drawable.placeholder)
                            .resize(500, 500)
                            .into(imageView)
                }.addOnFailureListener { exception: Exception? ->
                    progressBar!!.visibility = View.INVISIBLE
                    Log.e(TAG, "Error downloading", exception)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_details_transaction, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteTransaction()
                true
            }
            R.id.action_edit -> {
                editTransaction()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editTransaction() {
        val intent = Intent(this, AddTransactionActivity::class.java)
        intent.putExtra(TRANSACTION, transaction)
        startActivity(intent)
    }

    private fun deleteTransaction() {
        db!!.collection("users")
                .document(user!!.uid)
                .collection("transactions")
                .document(transaction!!.uid!!)
                .delete()
                .addOnSuccessListener { aVoid: Void? ->
                    Toast.makeText(this@DetailsActivity, "Transaction deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e: Exception? ->
                    Toast.makeText(this@DetailsActivity, "Deletion failed", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error deleting transaction", e)
                }
    }

    companion object {
        const val TRANSACTION = "transaction"
        private const val TAG = "DetailsActivity"
    }
}