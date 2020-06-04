package com.udacity.maluleque.meutako;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.udacity.maluleque.meutako.adapters.TransactionsAdapter;
import com.udacity.maluleque.meutako.model.Transaction;
import com.udacity.maluleque.meutako.preferences.PreferencesManager;
import com.udacity.maluleque.meutako.utils.DateUtils;
import com.udacity.maluleque.meutako.utils.NumberUtils;
import com.udacity.maluleque.meutako.widget.TransactionViewService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;


public class TransactionListFragment extends Fragment implements TransactionsAdapter.OnTransactionClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "TransactionListFragment";
    private static final String LAUNCH_PREF = "launch-prefs";

    @BindView(R.id.recyclerViewTransactions)
    RecyclerView recyclerView;

    @BindView(R.id.noDataLayout)
    LinearLayout noDataLayout;

    @BindView(R.id.noInternetLayout)
    LinearLayout noInternetLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.textViewAvailableAmount)
    TextView textViewAvailable;
    @BindView(R.id.textViewIncome)
    TextView textViewIncome;
    @BindView(R.id.textViewExpense)
    TextView textViewExpense;

    @BindView(R.id.resumeCardView)
    CardView resumeCardView;



    private int mParam1;
    private String dataMonth;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private FirebaseUser user;
    private FabButtonVisibilityListener fabButtonVisibilityListener;

    public TransactionListFragment() {

    }


    public static TransactionListFragment newInstance(int param1, String param2) {
        TransactionListFragment fragment = new TransactionListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            dataMonth = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);
        ButterKnife.bind(this, view);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager stickyHeaderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(stickyHeaderLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabButtonVisibilityListener.hideFabButton();
                } else {
                    fabButtonVisibilityListener.showFabButton();
                }
            }
        });


        initDataLoading(dataMonth);

        return view;
    }

    private void initDataLoading(String dataMonth) {
        getTransactions(dataMonth);
    }

    @OnClick(R.id.btnTryAgain)
    public void tryAgain() {
        initDataLoading(dataMonth);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fabButtonVisibilityListener = (FabButtonVisibilityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FabButtonVisibilityListener");
        }
    }

    void getTransactions(String dataMonth) {
        showProgressBar();
        long[] dateIntervals = DateUtils.getDateIntervals(dataMonth);
        Query query = db.collection("users")
                .document(user.getUid())
                .collection("transactions")
                .whereGreaterThanOrEqualTo("date", dateIntervals[0])
                .whereLessThanOrEqualTo("date", dateIntervals[1])
                .orderBy("date", Query.Direction.DESCENDING);

        registration = query.addSnapshotListener(
                (queryDocumentSnapshots, e) -> {

                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Transaction> transactions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        transactions.add(doc.toObject(Transaction.class));
                    }

                    populateResumeInfo(transactions);
                    updateWidget(transactions);

                    if (transactions.isEmpty()) {
                        showNoData();
                        return;
                    }

                    TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getContext(), transactions, this::onTransactionClick);
                    recyclerView.setAdapter(transactionsAdapter);
                    showData();


                });

    }

    private void updateWidget(List<Transaction> transactions) {
        PreferencesManager preferences = PreferencesManager.getInstance(getContext().getSharedPreferences(LAUNCH_PREF, MODE_PRIVATE));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Task<Void> task = database.getReference("widget").child(user.getUid()).child("transactions").setValue(getLastFiveTransactions(transactions));

        task.addOnSuccessListener(aVoid -> TransactionViewService.startActionUpdateViewTrsansaction(getContext())).addOnFailureListener(e -> {
            Log.e(TAG, "Saving failed.", e);
            preferences.setTransactions(getLastFiveTransactions(transactions));
            TransactionViewService.startActionUpdateViewTrsansaction(getContext());
        });



    }

    private String getLastFiveTransactions(List<Transaction> transactions) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Transaction transaction : transactions) {
            if (count < 5) {
                if (DateUtils.getDataDayMonth(transaction.getDate()).equals(DateUtils.getDataDayMonth(new Date().getTime()))) {
                    builder.append("\n")
                            .append(transaction.getCategory())
                            .append(" ")
                            .append(transaction.getType().equals("Income") ? " +" + NumberUtils.getFormattedAmount(transaction.getAmount()) : " -" + NumberUtils.getFormattedAmount(transaction.getAmount()));
                }
            }
            count++;
        }
        return builder.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration.remove();
    }

    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        noInternetLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    void showNoInternet() {
        progressBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        noInternetLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    void showNoData() {
        progressBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    void showData() {
        progressBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        noInternetLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTransactionClick(Transaction transaction) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.TRANSACTION, transaction);
        startActivity(intent);
    }


    public void openReportActivity(List<Transaction> transactions) {
        Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putParcelableArrayListExtra("transactions", new ArrayList<>(transactions));
        startActivity(intent);
    }

    private void populateResumeInfo(List<Transaction> transactions) {
        double totalIncome = getTotalMonthIncome(transactions);
        double totalExpense = getTotalMonthExpense(transactions);

        textViewExpense.setText(String.format("-%s", NumberUtils.getFormattedAmount(totalExpense)));
        textViewExpense.setTextColor(ContextCompat.getColor(getContext(), R.color.colorExpense));
        textViewIncome.setText(String.format("+%s", NumberUtils.getFormattedAmount(totalIncome)));
        textViewIncome.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIncome));
        textViewAvailable.setText(NumberUtils.getFormattedAmount(totalIncome - totalExpense));

        resumeCardView.setOnClickListener(view -> openReportActivity(transactions));
    }

    private double getTotalMonthExpense(List<Transaction> transactions) {
        double expense = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("Expense")) {
                expense = expense + transaction.getAmount();
            }
        }
        return expense;
    }

    private double getTotalMonthIncome(List<Transaction> transactions) {
        double income = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("Income")) {
                income = income + transaction.getAmount();
            }
        }
        return income;
    }

    public interface FabButtonVisibilityListener {
        void hideFabButton();

        void showFabButton();
    }

}
