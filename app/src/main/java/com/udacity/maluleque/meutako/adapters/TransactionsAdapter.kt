package com.udacity.maluleque.meutako.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.udacity.maluleque.meutako.R
import com.udacity.maluleque.meutako.model.Transaction
import com.udacity.maluleque.meutako.utils.DateUtils.getDataDayMonth
import com.udacity.maluleque.meutako.utils.NumberUtils.getFormattedAmount
import org.zakariya.stickyheaders.SectioningAdapter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(context: Context, private val transactions: List<Transaction>, onTransactionClickListener: OnTransactionClickListener) : SectioningAdapter() {
    private val context: Context
    private val sections: ArrayList<Section>
    private val onTransactionClickListener: OnTransactionClickListener
    override fun onCreateItemViewHolder(parent: ViewGroup, itemType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.transaction_item, parent, false)
        return TransactionItemViewHolder(v)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup, headerType: Int): HeaderSectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.header, parent, false)
        return HeaderSectionViewHolder(v)
    }

    private fun fillSections() {
        val list: List<String> = ArrayList(HashSet(datesSectionList()))
        Collections.sort(list, object : Comparator<String?> {
            var f: DateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            override fun compare(o1: String?, o2: String?): Int {
                return try {
                    f.parse(o2).compareTo(f.parse(o1))
                } catch (e: ParseException) {
                    throw IllegalArgumentException(e)
                }
            }
        })
        for (i in list.indices) {
            val section = Section()
            section.index = i
            section.header = list[i]
            sections.add(section)
            for (j in transactions.indices) {
                if (section.header.equals(getDataDayMonth(transactions[j].date), ignoreCase = true)) {
                    section.items.add(transactions[j])
                }
            }
        }
    }

    private fun datesSectionList(): List<String> {
        val sectionLabels: MutableList<String> = ArrayList()
        for (i in transactions.indices) {
            sectionLabels.add(getDataDayMonth(transactions[i].date))
        }
        return sectionLabels
    }

    override fun onBindItemViewHolder(viewHolder: ItemViewHolder, sectionIndex: Int, itemIndex: Int, itemType: Int) {
        val s = sections[sectionIndex].items[itemIndex]
        val holder = viewHolder as TransactionItemViewHolder
        if (s.type == "Income") {
            holder.textViewAmount!!.text = "+${getFormattedAmount(s.amount)}"
            holder.textViewAmount!!.setTextColor(ContextCompat.getColor(context, R.color.colorIncome))
        } else {
            holder.textViewAmount!!.text = "-${getFormattedAmount(s.amount)}"
            holder.textViewAmount!!.setTextColor(ContextCompat.getColor(context, R.color.colorExpense))
        }
        holder.textViewAmount!!.text = getFormattedAmount(s.amount)
        holder.textViewCategory!!.text = s.category
        holder.textViewType!!.text = s.type
    }

    override fun onBindHeaderViewHolder(viewHolder: HeaderViewHolder, sectionIndex: Int, headerType: Int) {
        val s = sections[sectionIndex]
        val header = viewHolder as HeaderSectionViewHolder
        header.textViewSection!!.text = s.header
    }

    override fun getNumberOfSections(): Int {
        return sections.size
    }

    override fun getNumberOfItemsInSection(sectionIndex: Int): Int {
        return sections[sectionIndex].items.size
    }

    override fun doesSectionHaveHeader(sectionIndex: Int): Boolean {
        return !TextUtils.isEmpty(sections[sectionIndex].header)
    }

    private inner class Section {
        var index = 0
        var header: String? = null
        var items: MutableList<Transaction> = ArrayList()
    }

    interface OnTransactionClickListener {
        fun onTransactionClick(transaction: Transaction?)
    }

    /*
     *
     * This ViewHolder Hold the transaction Information on click should open Transaction Details Screen
     * */
    inner class TransactionItemViewHolder(itemView: View) : ItemViewHolder(itemView), View.OnClickListener {
        @BindView(R.id.textViewCategory)
        lateinit var textViewCategory: TextView

        @BindView(R.id.textViewAmount)
        lateinit var textViewAmount: TextView

        @BindView(R.id.textViewType)
        lateinit var textViewType: TextView

        override fun onClick(view: View) {
            val position = adapterPosition
            val section = getSectionForAdapterPosition(position)
            val itemIndex = getPositionOfItemInSection(section, position)
            onTransactionClickListener.onTransactionClick(sections[section].items[itemIndex])
        }

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener(this)
        }
    }

    inner class HeaderSectionViewHolder(itemView: View?) : HeaderViewHolder(itemView) {
        @BindView(R.id.textViewSection)
        lateinit var textViewSection: TextView

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

    init {
        this.sections = ArrayList()
        this.onTransactionClickListener = onTransactionClickListener
        this.context = context
        fillSections()
    }
}