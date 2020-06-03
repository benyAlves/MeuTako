package com.udacity.maluleque.meutako.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.maluleque.meutako.R;
import com.udacity.maluleque.meutako.model.Transaction;
import com.udacity.maluleque.meutako.utils.DateUtils;
import com.udacity.maluleque.meutako.utils.NumberUtils;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsAdapter extends SectioningAdapter {

    private List<Transaction> transactions;
    private ArrayList<Section> sections;

    private static final int VIEW_TYPE_RESUME = 0;
    private static final int VIEW_TYPE_TRANSACTION = 1;
    private OnResumeClickListener onResumeClickListener;
    private OnTransactionClickListener onTransactionClickListener;

    public TransactionsAdapter(List<Transaction> transactions, OnResumeClickListener onResumeClickListener, OnTransactionClickListener onTransactionClickListener) {
        this.transactions = transactions;
        this.sections = new ArrayList<>();
        this.onResumeClickListener = onResumeClickListener;
        this.onTransactionClickListener = onTransactionClickListener;
        preecheSeccoes();
    }


    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {

        int layoutId;

        switch (itemType) {

            case VIEW_TYPE_RESUME: {
                layoutId = R.layout.result_history_item;
                View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                return new ResumeItemViewHolder(view);
            }

            case VIEW_TYPE_TRANSACTION: {
                layoutId = R.layout.transaction_item;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(layoutId, parent, false);
                return new TransactionItemViewHolder(v);
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + itemType);
        }

    }

    @Override
    public HeaderSectionViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.header, parent, false);
        return new HeaderSectionViewHolder(v);
    }


    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        if (itemIndex == 0 && sectionIndex == 0) {
            return VIEW_TYPE_RESUME;
        }
        return VIEW_TYPE_TRANSACTION;
    }

    private void preecheSeccoes() {
        List<String> list = new ArrayList<>(new HashSet<>(datesSectionList()));

        for (int i = 0; i < list.size(); i++) {
            Section section = new Section();
            section.index = i;
            section.header = list.get(i);
            sections.add(section);

            for (int j = 0; j < transactions.size(); j++) {
                if (section.header.equalsIgnoreCase(DateUtils.getDataDayMonth(transactions.get(j).getDate()))) {
                    section.items.add(transactions.get(j));
                }
            }
        }
    }

    private List<String> datesSectionList() {
        List<String> sectionLabels = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            sectionLabels.add(DateUtils.getDataDayMonth(transactions.get(i).getDate()));
        }
        return sectionLabels;
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {

        int viewType = getItemViewType(itemType);

        switch (viewType) {

            case VIEW_TYPE_RESUME: {
                final ResumeItemViewHolder holderResume = (ResumeItemViewHolder) viewHolder;
                double totalIncome = getTotalMonthIncome();
                double totalExpense = getTotalMonthExpense();
                holderResume.textViewExpense.setText(NumberUtils.getFormattedAmount(totalExpense));
                holderResume.textViewIncome.setText(NumberUtils.getFormattedAmount(totalIncome));
                holderResume.textViewAvailable.setText(NumberUtils.getFormattedAmount(totalIncome - totalExpense));

                break;
            }

            case VIEW_TYPE_TRANSACTION: {
                final Transaction s = sections.get(sectionIndex).items.get(itemIndex);
                final TransactionItemViewHolder holder = (TransactionItemViewHolder) viewHolder;

                holder.textViewAmount.setText(NumberUtils.getFormattedAmount(s.getAmount()));
                holder.textViewCategory.setText(s.getCategory());
                holder.textViewType.setText(s.getType());
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }


    }

    private double getTotalMonthExpense() {
        double expense = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("Expense")) {
                expense = expense + transaction.getAmount();
            }
        }
        return expense;
    }

    private double getTotalMonthIncome() {
        double income = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("Income")) {
                income = income + transaction.getAmount();
            }
        }
        return income;
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Section s = sections.get(sectionIndex);
        HeaderSectionViewHolder header = (HeaderSectionViewHolder) viewHolder;
        header.textViewSection.setText(s.header);
    }

    @Override
    public int getNumberOfSections() {
        return sections.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return sections.get(sectionIndex).items.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return !TextUtils.isEmpty(sections.get(sectionIndex).header);
    }

    private class Section {
        int index;
        String header;
        List<Transaction> items = new ArrayList<>();
    }

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public interface OnResumeClickListener {
        void onResumeClick(List<Transaction> transactions);
    }

    /*
     *
     * This ViewHolder Hold the transaction Information on click should open Transaction Details Screen
     * */

    public class TransactionItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener {

        @BindView(R.id.textViewCategory)
        TextView textViewCategory;
        @BindView(R.id.textViewAmount)
        TextView textViewAmount;
        @BindView(R.id.textViewType)
        TextView textViewType;

        public TransactionItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            final int section = TransactionsAdapter.this.getSectionForAdapterPosition(position);
            final int itemIndex = TransactionsAdapter.this.getPositionOfItemInSection(section, position);
            onTransactionClickListener.onTransactionClick(transactions.get(itemIndex));
        }

    }


    /*
     *
     * This ViewHolder Holds the all calculate Information about incomes and expenses on click should open Report Activity
     * */

    public class ResumeItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener {

        @BindView(R.id.textViewAvailableAmount)
        TextView textViewAvailable;
        @BindView(R.id.textViewIncome)
        TextView textViewIncome;
        @BindView(R.id.textViewExpense)
        TextView textViewExpense;

        public ResumeItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onResumeClickListener.onResumeClick(transactions);
        }
    }

    public class HeaderSectionViewHolder extends SectioningAdapter.HeaderViewHolder {

        @BindView(R.id.textViewSection)
        TextView textViewSection;

        public HeaderSectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
