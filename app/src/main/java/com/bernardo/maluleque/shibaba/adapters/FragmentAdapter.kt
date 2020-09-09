package com.bernardo.maluleque.shibaba.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bernardo.maluleque.shibaba.ReportFragment
import com.bernardo.maluleque.shibaba.TransactionListFragment
import com.bernardo.maluleque.shibaba.utils.DateUtils.lastMonth
import com.bernardo.maluleque.shibaba.utils.DateUtils.nextMonth
import com.bernardo.maluleque.shibaba.utils.DateUtils.thisMonth

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