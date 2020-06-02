package com.udacity.maluleque.meutako.adapters;

import android.content.Context;
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

    public TransactionsAdapter(Context context, List<Transaction> transactions) {
        this.transactions = transactions;
        this.sections = new ArrayList<>();
        preecheSeccoes();
    }


    @Override
    public TransactionItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.transaction_item, parent, false);
        return new TransactionItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.header, parent, false);
        return new HeaderViewHolder(v);
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
        final Transaction s = sections.get(sectionIndex).items.get(itemIndex);
        final TransactionItemViewHolder holder = (TransactionItemViewHolder) viewHolder;

        holder.textViewAmount.setText(NumberUtils.getFormattedAmount(s.getAmount()));
        holder.textViewCategory.setText(s.getCategory());
        holder.textViewType.setText(s.getType());

    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Section s = sections.get(sectionIndex);
        HeaderViewHolder header = (HeaderViewHolder) viewHolder;

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

    public class TransactionItemViewHolder extends SectioningAdapter.ItemViewHolder {

        @BindView(R.id.textViewCategory)
        TextView textViewCategory;
        @BindView(R.id.textViewAmount)
        TextView textViewAmount;
        @BindView(R.id.textViewType)
        TextView textViewType;

        public TransactionItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }


    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {

        @BindView(R.id.textViewSection)
        TextView textViewSection;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
