package com.udacity.maluleque.meutako.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.udacity.maluleque.meutako.ReportFragment
import com.udacity.maluleque.meutako.TransactionListFragment
import com.udacity.maluleque.meutako.utils.DateUtils.lastMonth
import com.udacity.maluleque.meutako.utils.DateUtils.nextMonth
import com.udacity.maluleque.meutako.utils.DateUtils.thisMonth

class FragmentAdapter(fm: FragmentManager?, private val months: List<String>, private val type: Int) : FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return if (type == 0) {
            TransactionListFragment.newInstance(position, months[position])
        } else ReportFragment.newInstance(position, months[position])
    }

    override fun getCount(): Int {
        return months.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (months[position] == thisMonth()) {
            return "This Month"
        } else if (months[position] == lastMonth()) {
            return "Last Month"
        } else if (months[position] == nextMonth()) {
            return "Next Month"
        }
        return months[position]
    }

}