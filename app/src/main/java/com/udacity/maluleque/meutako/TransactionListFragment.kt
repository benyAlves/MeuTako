package com.udacity.maluleque.meutako

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Query
import com.udacity.maluleque.meutako.adapters.TransactionsAdapter
import com.udacity.maluleque.meutako.adapters.TransactionsAdapter.OnTransactionClickListener
import com.udacity.maluleque.meutako.model.Transaction
import com.udacity.maluleque.meutako.preferences.PreferencesManager
import com.udacity.maluleque.meutako.utils.DateUtils
import com.udacity.maluleque.meutako.utils.NumberUtils
import com.udacity.maluleque.meutako.utils.Status
import com.udacity.maluleque.meutako.viewmodel.TransactionViewModel
import com.udacity.maluleque.meutako.widget.TransactionViewService
import java.util.*
import javax.inject.Inject

class TransactionListFragment : Fragment(), OnTransactionClickListener {

    @BindView(R.id.recyclerViewTransactions)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.noDataLayout)
    lateinit var noDataLayout: LinearLayout

    @BindView(R.id.noInternetLayout)
    lateinit var noInternetLayout: LinearLayout

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.textViewAvailableAmount)
    lateinit var textViewAvailable: TextView

    @BindView(R.id.textViewIncome)
    lateinit var textViewIncome: TextView

    @BindView(R.id.textViewExpense)
    lateinit var textViewExpense: TextView

    @BindView(R.id.resumeCardView)
    lateinit var resumeCardView: CardView

    private var mParam1 = 0
    private var dataMonth: String? = null
    private var user: FirebaseUser? = null
    private var fabButtonVisibilityListener: FabButtonVisibilityListener? = null
    private lateinit var transactionViewModel: TransactionViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getInt(ARG_PARAM1)
            dataMonth = requireArguments().getString(ARG_PARAM2)
        }
        user = FirebaseAuth.getInstance().currentUser

        (activity?.application as App).appComponent.inject(this)

        transactionViewModel = ViewModelProvider(this, viewModelFactory).get(TransactionViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)
        ButterKnife.bind(this, view)
        recyclerView.setHasFixedSize(true)
        val stickyHeaderLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = stickyHeaderLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fabButtonVisibilityListener!!.hideFabButton()
                } else {
                    fabButtonVisibilityListener!!.showFabButton()
                }
            }
        })
        initDataLoading(dataMonth)
        return view
    }

    private fun initDataLoading(dataMonth: String?) {
        getTransactions(dataMonth)
    }

    @OnClick(R.id.btnTryAgain)
    fun tryAgain() {
        initDataLoading(dataMonth)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fabButtonVisibilityListener = try {
            context as FabButtonVisibilityListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement FabButtonVisibilityListener")
        }
    }

    fun getTransactions(dataMonth: String?) {
        showProgressBar()
        val dateIntervals = DateUtils.getDateIntervals(dataMonth)

        transactionViewModel.getTransactions(user!!.uid, dateIntervals[0], dateIntervals[1], Query.Direction.DESCENDING)
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    when (it.status) {
                        Status.LOADING -> {
                            showProgressBar()
                        }
                        Status.SUCCESS -> {
                            val transactions = it.data
                            if (transactions.isNotEmpty()) {
                                populateResumeInfo(transactions)
                                updateWidget(transactions)
                                val transactionsAdapter = TransactionsAdapter(requireContext(), transactions, this)
                                recyclerView!!.adapter = transactionsAdapter
                                showData()
                            } else {
                                showNoData()
                            }
                        }
                        Status.ERROR -> {
                            showNoData()
                        }
                    }
                })

        /* registration = query.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
             if (e != null) {
                 Log.e(TAG, "Listen failed.", e)
                 return@addSnapshotListener
             }
             val transactions: MutableList<Transaction> = ArrayList()
             for (doc in queryDocumentSnapshots!!) {
                 transactions.add(doc.toObject(Transaction::class.java))
             }
             populateResumeInfo(transactions)
             updateWidget(transactions)
             if (transactions.isEmpty()) {
                 showNoData()
                 return@addSnapshotListener
             }
             val transactionsAdapter = TransactionsAdapter(requireContext(), transactions, this)
             recyclerView!!.adapter = transactionsAdapter
             showData()
         }*/
    }

    private fun updateWidget(transactions: List<Transaction>) {
        val preferences = PreferencesManager.getInstance(requireContext().getSharedPreferences(LAUNCH_PREF, Context.MODE_PRIVATE))
        val database = FirebaseDatabase.getInstance()
        val task = database.getReference("widget").child(user!!.uid).child("transactions").setValue(getLastFiveTransactions(transactions))
        task.addOnSuccessListener { aVoid: Void? -> TransactionViewService.startActionUpdateViewTrsansaction(context) }.addOnFailureListener { e: Exception? ->
            Log.e(TAG, "Saving failed.", e)
            preferences!!.transactions = getLastFiveTransactions(transactions)
            TransactionViewService.startActionUpdateViewTrsansaction(context)
        }
    }

    private fun getLastFiveTransactions(transactions: List<Transaction>): String {
        val builder = StringBuilder()
        var count = 0
        for (transaction in transactions) {
            if (count < 5) {
                if (DateUtils.getDataDayMonth(transaction.date) == DateUtils.getDataDayMonth(Date().time)) {
                    builder.append("\n")
                            .append(transaction.category)
                            .append(" ")
                            .append(if (transaction.type == "Income") " +" + NumberUtils.getFormattedAmount(transaction.amount) else " -" + NumberUtils.getFormattedAmount(transaction.amount))
                }
            }
            count++
        }
        return builder.toString()
    }

    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        noDataLayout.visibility = View.INVISIBLE
        noInternetLayout.visibility = View.INVISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    fun showNoInternet() {
        progressBar.visibility = View.INVISIBLE
        noDataLayout.visibility = View.INVISIBLE
        noInternetLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    fun showNoData() {
        progressBar.visibility = View.INVISIBLE
        noDataLayout.visibility = View.VISIBLE
        noInternetLayout.visibility = View.INVISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    fun showData() {
        progressBar.visibility = View.INVISIBLE
        noDataLayout.visibility = View.INVISIBLE
        noInternetLayout.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
    }

    override fun onTransactionClick(transaction: Transaction?) {
        val intent = Intent(activity, DetailsActivity::class.java)
        intent.putExtra(DetailsActivity.Companion.TRANSACTION, transaction)
        startActivity(intent)
    }

    fun openReportActivity(transactions: List<Transaction>?) {
        val intent = Intent(activity, ReportActivity::class.java)
        intent.putParcelableArrayListExtra("transactions", ArrayList(transactions!!))
        startActivity(intent)
    }

    private fun populateResumeInfo(transactions: List<Transaction>) {
        val totalIncome = getTotalMonthIncome(transactions)
        val totalExpense = getTotalMonthExpense(transactions)
        textViewExpense.text = String.format("-%s", NumberUtils.getFormattedAmount(totalExpense))
        textViewExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorExpense))
        textViewIncome.text = String.format("+%s", NumberUtils.getFormattedAmount(totalIncome))
        textViewIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorIncome))
        textViewAvailable.text = NumberUtils.getFormattedAmount(totalIncome - totalExpense)
        resumeCardView.setOnClickListener { view: View? -> openReportActivity(transactions) }
    }

    private fun getTotalMonthExpense(transactions: List<Transaction>): Double {
        var expense = 0.0
        for (transaction in transactions) {
            if (transaction.type == "Expense") {
                expense = expense + transaction.amount
            }
        }
        return expense
    }

    private fun getTotalMonthIncome(transactions: List<Transaction>): Double {
        var income = 0.0
        for (transaction in transactions) {
            if (transaction.type == "Income") {
                income = income + transaction.amount
            }
        }
        return income
    }

    interface FabButtonVisibilityListener {
        fun hideFabButton()
        fun showFabButton()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val TAG = "TransactionListFragment"
        private const val LAUNCH_PREF = "launch-prefs"

        @JvmStatic
        fun newInstance(param1: Int, param2: String?): TransactionListFragment {
            val fragment = TransactionListFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}