package com.udacity.maluleque.meutako;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.udacity.maluleque.meutako.adapters.TransactionsAdapter;
import com.udacity.maluleque.meutako.model.Transaction;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TransactionListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "TransactionListFragment";
    @BindView(R.id.recyclerViewTransactions)
    RecyclerView recyclerView;
    private int mParam1;
    private String mParam2;

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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "onCreate " + mParam2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView" + mParam2);
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        StickyHeaderLayoutManager stickyHeaderLayoutManager = new StickyHeaderLayoutManager();
        recyclerView.setLayoutManager(stickyHeaderLayoutManager);

       /* stickyHeaderLayoutManager.setHeaderPositionChangedCallback(new StickyHeaderLayoutManager.HeaderPositionChangedCallback() {
            @Override
            public void onHeaderPositionChanged(int sectionIndex, View header, StickyHeaderLayoutManager.HeaderPosition oldPosition, StickyHeaderLayoutManager.HeaderPosition newPosition) {
                Log.i(TAG, "onHeaderPositionChanged: section: " + sectionIndex + " -> old: " + oldPosition.name() + " new: " + newPosition.name());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    boolean elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY;
                    header.setElevation(elevated ? 8 : 0);
                }
            }
        });
*/

        List<Transaction> transactions = new ArrayList<>();
        Transaction t = new Transaction();
        t.setDate(new Date().getTime());
        t.setType("Salary");
        t.setAmount(76000);

        Transaction t1 = new Transaction();
        t1.setDate(new Date().getTime());
        t1.setType("Salary");
        t1.setAmount(200600);

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        Transaction t3 = new Transaction();
        t3.setDate(calendar.getTime().getTime());
        t3.setType("Transportation");
        t3.setAmount(67000);

        Transaction t4 = new Transaction();
        t4.setDate(calendar.getTime().getTime());
        t4.setType("Groceries");
        t4.setAmount(9000);

        Transaction t5 = new Transaction();
        t5.setDate(calendar.getTime().getTime());
        t5.setType("Entertainment");
        t5.setAmount(700);

        transactions.add(t);
        transactions.add(t1);
        transactions.add(t3);
        transactions.add(t5);
        transactions.add(t4);

        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getContext(), transactions);

        recyclerView.setAdapter(transactionsAdapter);


        return view;

    }


}
