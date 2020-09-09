package com.bernardo.maluleque.shibaba

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.bernardo.maluleque.shibaba.model.Transaction
import com.bernardo.maluleque.shibaba.utils.DateUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

class ReportFragment : Fragment() {
    @JvmField
    @BindView(R.id.pieChart)
    var pieChart: PieChart? = null

    @JvmField
    @BindView(R.id.lineChart)
    var lineChart: LineChart? = null

    @JvmField
    @BindView(R.id.barChart)
    var barChart: BarChart? = null
    private var mParam1 = 0
    private var dataMonth: String? = null
    private var db: FirebaseFirestore? = null
    private var registration: ListenerRegistration? = null
    private var user: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getInt(ARG_PARAM1)
            dataMonth = requireArguments().getString(ARG_PARAM2)
        }
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        ButterKnife.bind(this, view)
        getTransactions(dataMonth)
        return view
    }

    fun getTransactions(dataMonth: String?) {
        val dateIntervals = DateUtils.getDateIntervals(dataMonth)
        val query = db!!.collection("users")
                .document(user!!.uid)
                .collection("transactions")
                .whereGreaterThanOrEqualTo("date", dateIntervals[0])
                .whereLessThanOrEqualTo("date", dateIntervals[1])
                .orderBy("date", Query.Direction.DESCENDING)
        registration = query.addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
            if (e != null) {
                Log.e(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            val transactions: MutableList<Transaction> = ArrayList()
            for (doc in queryDocumentSnapshots!!) {
                transactions.add(doc.toObject(Transaction::class.java))
            }
            if (transactions.isEmpty()) {
                return@addSnapshotListener
            }
            showData(transactions)
        }
    }

    private fun showData(transactions: List<Transaction>) {
        setPieChartData(transactions)
        setBarData(transactions)
        setLine2ChartData(transactions)
    }

    private fun setBarData(transactions: List<Transaction>) {
        val entries: MutableList<BarEntry> = ArrayList()
        val entriesExpense: MutableList<BarEntry> = ArrayList()
        val sumByExpenses = transactions.stream().filter { transaction: Transaction -> transaction.type == "Expense" }
                .collect(Collectors.groupingBy(Function { obj: Transaction -> obj.type }, Collectors.summingDouble { obj: Transaction -> obj.amount }))
        val sumByIncomes = transactions.stream().filter { transaction: Transaction -> transaction.type == "Income" }
                .collect(Collectors.groupingBy(Function { obj: Transaction -> obj.type }, Collectors.summingDouble { obj: Transaction -> obj.amount }))
        for ((_, value) in sumByExpenses) {
            entries.add(BarEntry(0f, value.toFloat()))
        }
        for ((_, value) in sumByIncomes) {
            entriesExpense.add(BarEntry(2f, value.toFloat()))
        }
        val set = BarDataSet(entries, "Total Income")
        set.color = Color.GREEN
        val set2 = BarDataSet(entriesExpense, "Total Expense")
        set2.color = Color.RED
        val data = BarData(set, set2)
        data.barWidth = 0.9f // set custom bar width
        val description = Description()
        description.text = "Totals expenses and incomes"
        barChart!!.description = description
        barChart!!.data = data
        barChart!!.setFitBars(true) // make the x-axis fit exactly all bars
        barChart!!.invalidate() // refresh
    }

    private fun setPieChartData(transactions: List<Transaction>) {
        val entries = ArrayList<PieEntry>()
        val sum = transactions.stream().filter { transaction: Transaction -> transaction.type == "Expense" }.collect(Collectors.groupingBy(Function { obj: Transaction -> obj.category }, Collectors.summingDouble { obj: Transaction -> obj.amount }))
        sum.entries.forEach(Consumer {
            entries.add(PieEntry(it.value.toFloat(), it.key))
        })
        val dataSet = PieDataSet(entries, "Categories")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        dataSet.selectionShift = 0f
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(R.color.colorPrimaryText)
        pieChart!!.data = data

        // undo all highlights
        pieChart!!.highlightValues(null)
        pieChart!!.centerText = "Expenses by category"
        val description = Description()
        description.text = "Check where you expend the most"
        pieChart!!.description = description
        pieChart!!.invalidate()
    }

    private fun setLine2ChartData(transactions: List<Transaction>) {
        val sumByTypeAndDate = transactions.stream().filter { transaction: Transaction -> transaction.type == "Expense" }
                .collect(Collectors.groupingBy(Function { obj: Transaction -> obj.formattedDate }, Collectors.summingDouble { obj: Transaction -> obj.amount }))
        val sumByIncomeAndDate = transactions.stream().filter { transaction: Transaction -> transaction.type == "Income" }
                .collect(Collectors.groupingBy(Function { obj: Transaction -> obj.formattedDate }, Collectors.summingDouble { obj: Transaction -> obj.amount }))
        val values1 = ArrayList<Entry>()
        var ds = 0
        for ((_, value) in sumByTypeAndDate) {
            values1.add(Entry(ds.toFloat(), value.toFloat()))
            ds = ds + 1
        }
        val values2 = ArrayList<Entry>()
        for ((_, value) in sumByIncomeAndDate) {
            values2.add(Entry(ds.toFloat(), value.toFloat()))
            ds = ds + 1
        }
        val setComp1 = LineDataSet(values1, "Expenses")
        setComp1.axisDependency = YAxis.AxisDependency.LEFT
        setComp1.color = Color.RED
        setComp1.setCircleColor(Color.WHITE)
        val setComp2 = LineDataSet(values2, "Incomes")
        setComp2.axisDependency = YAxis.AxisDependency.LEFT
        setComp2.color = Color.GREEN
        setComp2.setCircleColor(Color.GREEN)
        val dataSets: MutableList<ILineDataSet> = ArrayList()
        dataSets.add(setComp1)
        dataSets.add(setComp2)
        val data = LineData(dataSets)
        val xAxis = lineChart!!.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        val description = Description()
        description.text = "Check daily expenses and incomes"
        lineChart!!.description = description
        lineChart!!.data = data
        lineChart!!.invalidate() // refresh
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val TAG = "ReportFragment"

        @JvmStatic
        fun newInstance(param1: Int, param2: String?): ReportFragment {
            val fragment = ReportFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        fun <T> distinctByKey(keyExtractor: Function<in T, *>): Predicate<T> {
            val seen: MutableSet<Any> = ConcurrentHashMap.newKeySet()
            return Predicate { t: T -> seen.add(keyExtractor.apply(t)) }
        }
    }
}