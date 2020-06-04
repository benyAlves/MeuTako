package com.udacity.maluleque.meutako.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.udacity.maluleque.meutako.ReportFragment;
import com.udacity.maluleque.meutako.TransactionListFragment;
import com.udacity.maluleque.meutako.utils.DateUtils;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private final List<String> months;
    private final int type;

    public FragmentAdapter(FragmentManager fm, List<String> months, int type) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.months = months;
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        if (type == 0) {
            return TransactionListFragment.newInstance(position, months.get(position));
        }
        return ReportFragment.newInstance(position, months.get(position));
    }

    @Override
    public int getCount() {
        return months.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (months.get(position).equals(DateUtils.thisMonth())) {
            return "This Month";
        } else if (months.get(position).equals(DateUtils.lastMonth())) {
            return "Last Month";
        } else if (months.get(position).equals(DateUtils.nextMonth())) {
            return "Next Month";
        }
        return months.get(position);
    }

}
