package com.udacity.maluleque.meutako;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.udacity.maluleque.meutako.model.Transaction;
import com.udacity.maluleque.meutako.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReportFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ReportFragment";

    @BindView(R.id.pieChart)
    PieChart pieChart;
    @BindView(R.id.lineChart)
    LineChart lineChart;
    @BindView(R.id.barChart)
    BarChart barChart;

    private int mParam1;
    private String dataMonth;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private FirebaseUser user;


    public ReportFragment() {

    }


    public static ReportFragment newInstance(int param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
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
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, view);

        getTransactions(dataMonth);
        return view;

    }

    void getTransactions(String dataMonth) {
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

                    if (transactions.isEmpty()) {
                        return;
                    }

                    showData(transactions);


                });

    }

    private void showData(List<Transaction> transactions) {
        setPieChartData(transactions);
        setBarData(transactions);
        setLine2ChartData(transactions);
    }

    private void setBarData(List<Transaction> transactions) {
        List<BarEntry> entries = new ArrayList<>();
        List<BarEntry> entriesExpense = new ArrayList<>();

        Map<String, Double> sumByExpenses = transactions.stream().filter(transaction -> transaction.getType().equals("Expense"))
                .collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));

        Map<String, Double> sumByIncomes = transactions.stream().filter(transaction -> transaction.getType().equals("Income"))
                .collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));

        for (Map.Entry<String, Double> transaction : sumByExpenses.entrySet()) {
            entries.add(new BarEntry(0, new Double(transaction.getValue()).floatValue()));
        }


        for (Map.Entry<String, Double> transaction : sumByIncomes.entrySet()) {
            entriesExpense.add(new BarEntry(2, new Double(transaction.getValue()).floatValue()));
        }


        BarDataSet set = new BarDataSet(entries, "Total Income");
        set.setColor(Color.GREEN);
        BarDataSet set2 = new BarDataSet(entriesExpense, "Total Expense");
        set2.setColor(Color.RED);

        BarData data = new BarData(set, set2);
        data.setBarWidth(0.9f); // set custom bar width
        Description description = new Description();
        description.setText("Totals expenses and incomes");
        barChart.setDescription(description);
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); // refresh
    }

    private void setPieChartData(List<Transaction> transactions) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        Map<String, Double> sum = transactions.stream().filter(transaction -> transaction.getType().equals("Expense")).collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
        sum.entrySet().forEach(x -> entries.add(new PieEntry(x.getValue().floatValue(), x.getKey())));

        PieDataSet dataSet = new PieDataSet(entries, "Categories");

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(R.color.colorPrimaryText);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.setCenterText("Expenses by category");
        Description description = new Description();
        description.setText("Check where you expend the most");
        pieChart.setDescription(description);
        pieChart.invalidate();
    }

    private void setLine2ChartData(List<Transaction> transactions) {

        Map<String, Double> sumByTypeAndDate = transactions.stream().filter(transaction -> transaction.getType().equals("Expense"))
                .collect(Collectors.groupingBy(Transaction::getFormattedDate, Collectors.summingDouble(Transaction::getAmount)));

        Map<String, Double> sumByIncomeAndDate = transactions.stream().filter(transaction -> transaction.getType().equals("Income"))
                .collect(Collectors.groupingBy(Transaction::getFormattedDate, Collectors.summingDouble(Transaction::getAmount)));


        ArrayList<Entry> values1 = new ArrayList<>();
        int ds = 0;

        for (Map.Entry<String, Double> transaction : sumByTypeAndDate.entrySet()) {
            values1.add(new Entry(ds, new Double(transaction.getValue()).floatValue()));
            ds = ds + 1;
        }


        ArrayList<Entry> values2 = new ArrayList<>();

        for (Map.Entry<String, Double> transaction : sumByIncomeAndDate.entrySet()) {
            values2.add(new Entry(ds, new Double(transaction.getValue()).floatValue()));
            ds = ds + 1;
        }

        LineDataSet setComp1 = new LineDataSet(values1, "Expenses");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(Color.RED);
        setComp1.setCircleColor(Color.WHITE);

        LineDataSet setComp2 = new LineDataSet(values2, "Incomes");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setColor(Color.GREEN);
        setComp2.setCircleColor(Color.GREEN);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        LineData data = new LineData(dataSets);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1


        Description description = new Description();
        description.setText("Check daily expenses and incomes");
        lineChart.setDescription(description);

        lineChart.setData(data);
        lineChart.invalidate(); // refresh

    }

}
