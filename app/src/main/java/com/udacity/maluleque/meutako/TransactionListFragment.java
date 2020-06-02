package com.udacity.maluleque.meutako;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.udacity.maluleque.meutako.adapters.TransactionsAdapter;
import com.udacity.maluleque.meutako.model.Transaction;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TransactionListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "TransactionListFragment";

    @BindView(R.id.recyclerViewTransactions)
    RecyclerView recyclerView;

    @BindView(R.id.noDataLayout)
    LinearLayout noDataLayout;

    @BindView(R.id.noInternetLayout)
    LinearLayout noInternetLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

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
        Log.d(TAG, "onCreate " + dataMonth);
        Log.d(TAG, "onCreate " + mParam1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);
        ButterKnife.bind(this, view);


        recyclerView.setHasFixedSize(true);
        StickyHeaderLayoutManager stickyHeaderLayoutManager = new StickyHeaderLayoutManager();
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

        stickyHeaderLayoutManager.setHeaderPositionChangedCallback(new StickyHeaderLayoutManager.HeaderPositionChangedCallback() {
            @Override
            public void onHeaderPositionChanged(int sectionIndex, View header, StickyHeaderLayoutManager.HeaderPosition oldPosition, StickyHeaderLayoutManager.HeaderPosition newPosition) {
                Log.i(TAG, "onHeaderPositionChanged: section: " + sectionIndex + " -> old: " + oldPosition.name() + " new: " + newPosition.name());
                boolean elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY;
                header.setElevation(elevated ? 8 : 0);
            }
        });

        initDataLoading();

        return view;
    }

    private void initDataLoading() {
        // if(NetworkUtils.hasInternetConnection(getContext())) {
        getTransactions();
        /*}else {
            showNoInternet();
        }*/
    }

    @OnClick(R.id.btnTryAgain)
    public void tryAgain() {
        initDataLoading();
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

    void getTransactions() {
        showProgressBar();

   /*     TODO SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");

        try {
            Date convertedDate = null; convertedDate = dateFormat.parse(dataMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(convertedDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));*/

        Query query = db.collection("users")
                .document(user.getUid())
                .collection("transactions");

        registration = query.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            return;
                        }

                        List<Transaction> transactions = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            transactions.add(doc.toObject(Transaction.class));
                        }

                        if (transactions.isEmpty()) {
                            showNoData();
                            return;
                        }

                        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getContext(), transactions);
                        recyclerView.setAdapter(transactionsAdapter);
                        showData();

                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "fragment destroyed");
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

    public interface FabButtonVisibilityListener {
        void hideFabButton();

        void showFabButton();
    }

}
